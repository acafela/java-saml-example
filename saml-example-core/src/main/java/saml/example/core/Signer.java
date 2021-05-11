package saml.example.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.springframework.util.StringUtils.hasText;

public class Signer {

    private final static Logger LOGGER = LoggerFactory.getLogger(SAMLMarshaller.class);

    private static DocumentBuilder documentBuilder;
    private static XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");

    static {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            documentBuilder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOGGER.error("Failed to initialize document builder.", e);
        }
    }

    OutputStream envelopeSignature(InputStream documentIs, KeyStore.PrivateKeyEntry keyEntry) {
        Document document = parseDocument(documentIs);
        Element response = document.getDocumentElement();
        requireNonNull(response, "Document is empty.");
        String refId = getAndSetRefId(response);
        SignedInfo signedInfo = createSignedInfo(refId);

        X509Certificate cert = (X509Certificate) keyEntry.getCertificate();
        KeyInfoFactory kif = signatureFactory.getKeyInfoFactory();
        X509Data xd = kif.newX509Data(Collections.singletonList(cert));
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

        DOMSignContext domSignContext = new DOMSignContext(keyEntry.getPrivateKey(), response);
        XMLSignature signature = signatureFactory.newXMLSignature(signedInfo, ki);
        try {
            signature.sign(domSignContext);
            OutputStream os = new ByteArrayOutputStream();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.transform(new DOMSource(document), new StreamResult(os));
            return os;
        } catch (MarshalException | XMLSignatureException | TransformerException e) {
            throw new IllegalStateException("Can't envelope signature", e);
        }
    }

    private Document parseDocument(InputStream documentIs) {
        try {
            return documentBuilder.parse(documentIs);
        } catch (SAXException | IOException e) {
            LOGGER.error("Failed to parse input document.", e);
            throw new IllegalArgumentException("Failed to parse input document.");
        }
    }

    private SignedInfo createSignedInfo(String refId) {
        Transform transform = null;
        try {
            transform = signatureFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null);
            Reference reference = signatureFactory.newReference(refId , signatureFactory.newDigestMethod(DigestMethod.SHA256, null), Collections.singletonList(transform), null, null);
            CanonicalizationMethod canonicalizationMethod = signatureFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null);
            SignatureMethod sigMethod = signatureFactory.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", null);
            return signatureFactory.newSignedInfo(canonicalizationMethod, sigMethod, Collections.singletonList(reference));
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            LOGGER.error("Failed to create SignedInfo instance.", e);
            throw new RuntimeException("Failed to create SignedInfo instance.");
        }
    }

    /**
     * If there is an ID attribute value, enable the ID attribute and return the reference id.
     * Otherwise returns an empty string.
     */
    private String getAndSetRefId(Element response) {
        String responseId = response.getAttribute("ID");
        if (hasText(responseId)) {
            response.setIdAttribute("ID", true);
            return "#" + responseId;
        }
        return "";
    }
}