package saml.example.sp;

import org.joda.time.DateTime;
import org.opensaml.saml2.core.*;
import org.opensaml.xml.security.credential.BasicCredential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.signature.X509Certificate;
import org.opensaml.xml.signature.X509Data;
import org.opensaml.xml.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saml.example.core.SAMLObjectUtils;

import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.List;

public class AssertionConsumer {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public SpUser consume(Response samlResponse) throws CertificateException, ValidationException {

        validateSignature(samlResponse);
        checkAuthnInstant(samlResponse, 30);

        Assertion assertion = samlResponse.getAssertions().get(0);
        LOG.debug("Assertion[{}]", SAMLObjectUtils.samlObjectToString(assertion));

        return createUser(assertion);
    }

    private SpUser createUser(Assertion assertion) {
        String nameID = assertion.getSubject().getNameID().getValue();
        AttributeStatement attributeStatement = assertion.getAttributeStatements().get(0);
        List<Attribute> attributes = attributeStatement.getAttributes();
        return new SpUser(nameID, attributes);
    }

    private void validateSignature(Response samlResponse) throws CertificateException, ValidationException {

        Signature signature = samlResponse.getSignature();
        PublicKey publicKey = extractPublicKey(signature);

        SignatureValidator validator = createValidator(publicKey);

        try {
            validator.validate(samlResponse.getSignature());
            LOG.debug("Signature validation success");
        } catch (ValidationException e) {
            LOG.error("Signature validation fail.", e);
            throw e;
        }
    }

    private PublicKey extractPublicKey(Signature signature) throws CertificateException {

        X509Data x509Data = signature.getKeyInfo().getX509Datas().get(0);
        X509Certificate cert = x509Data.getX509Certificates().get(0);

        String wrappedCert = wrapBase64String(cert.getValue());
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        Certificate certificate = certFactory.generateCertificate(new ByteArrayInputStream(wrappedCert.getBytes()));

        return certificate.getPublicKey();
    }

    private String wrapBase64String(String base64String) {

        int lineLength = 64;

        char[] rawArr = base64String.toCharArray();

        int wrappedArrLength = rawArr.length + (int)Math.ceil(rawArr.length / 64d) - 1;
        char[] wrappedArr = new char[wrappedArrLength];

        int destPosition = 0;
        for(int i=0; i<rawArr.length; i+=lineLength) {
            if(rawArr.length - i > lineLength) {
                System.arraycopy(rawArr, i, wrappedArr, destPosition, lineLength);
                destPosition += lineLength;
                wrappedArr[destPosition] = '\n';
                destPosition += 1;
            } else {
                System.arraycopy(rawArr, i, wrappedArr, destPosition, rawArr.length - i);
            }
        }

        return "-----BEGIN CERTIFICATE-----\n" + String.valueOf(wrappedArr) + "\n-----END CERTIFICATE-----";
    }

    private SignatureValidator createValidator(PublicKey publicKey) {
        BasicCredential credential = new BasicCredential();
        credential.setPublicKey(publicKey);
        return new SignatureValidator(credential);
    }

    private void checkAuthnInstant(Response samlResponse, int validMin) throws ValidationException {

        Assertion assertion = samlResponse.getAssertions().get(0);
        AuthnStatement auhtnStatement = assertion.getAuthnStatements().get(0);
        DateTime authnInstant = auhtnStatement.getAuthnInstant();
        LOG.debug("AuthnInstant[{}]", authnInstant);

        DateTime validTime = authnInstant.plusMinutes(validMin);

        if(DateTime.now().compareTo(validTime) > 0){
            throw new ValidationException("AuthnInstant time out : " + authnInstant);
        }

    }

}
