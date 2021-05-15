    package saml.example.core;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.naming.Name;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

    public class JAXBTest {

    @Test
    public void responseTest() throws JAXBException {
        SubjectConfirmationData subjectConfirmationData = new SubjectConfirmationData()
                .inResponseTo(UUID.randomUUID().toString())
                .notOnOrAfter(LocalDateTime.now())
                .recipient("http://sp.com/acs");
        SubjectConfirmation subjectConfirmation = new SubjectConfirmation()
                .subjectConfirmationData(subjectConfirmationData);
        Subject subject = new Subject().nameID(new NameID("yshwang"))
                .subjectConfirmation(subjectConfirmation);

        Assertion assertion = new Assertion().subject(subject);

        Response response = new Response().assertion(assertion).status(Status.success());

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        SAMLMarshaller.instance().marshal(response, os);

//        System.out.println(os.toString());

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

        try {
            createSignature(is, response.id());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createSignature(ByteArrayInputStream is, String refId) throws Exception {
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        // Instantiate the document to be signed.
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document purchaseOrder = dbf.newDocumentBuilder().parse(is);

//        System.out.println(((Element) purchaseOrder).getTagName());

        Element response = purchaseOrder.getDocumentElement();
        requireNonNull(response, "Response element is empty");
        System.out.println(response.getTagName());


//        NodeList nl = purchaseOrder.getElementsByTagName("samlp:Response");
//        requireNonNull(nl, "Response element is empty");
//        Node node = nl.item(0);
        response.setIdAttribute("ID", true);
        String id = response.getAttribute("ID");

        System.out.println(">> " + id);

        // Create a Reference to the enveloped document (in this case, you are singing the whole document, so a URI of "" signifiles
        // that, and also specify the SHA1 digest algorithm and the ENVELOPED Transform.
        Transform transform = fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null);
        Reference ref = fac.newReference("#" + id, fac.newDigestMethod(DigestMethod.SHA256, null), Collections.singletonList(transform), null, null);

        // Create the SignedInfo
        CanonicalizationMethod canonicalizationMethod = fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null);
//        SignatureMethod sigMethod = fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null);
        SignatureMethod sigMethod = fac.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", null);
        SignedInfo si = fac.newSignedInfo(canonicalizationMethod, sigMethod, Collections.singletonList(ref));

        // Load KeyStore and get the signing key and certificate.
        KeyStore ks = KeyStore.getInstance("JKS");

        ks.load(new FileInputStream("./mykeystore.jks"), "changeit".toCharArray());
        KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry("myKey", new KeyStore.PasswordProtection("changeit".toCharArray()));
        X509Certificate cert = (X509Certificate) keyEntry.getCertificate();

        // Create the KeyInfo containing the X509Data.
        KeyInfoFactory kif = fac.getKeyInfoFactory();
        List<Object> x509Content = new ArrayList<>();
//        x509Content.add(cert.getSubjectX500Principal().getName());
        x509Content.add(cert);
        X509Data xd = kif.newX509Data(x509Content);
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));



        // Create a DOMSignContext and specify the RSA PrivateKey and location of resulting XMLSignature's parent element.
        DOMSignContext dsc = new DOMSignContext(keyEntry.getPrivateKey(), purchaseOrder.getDocumentElement());

        // Create the XMLSignature, but don't sign it yet.
        XMLSignature signature = fac.newXMLSignature(si, ki);

        // Marshal, generate, and sign the enveloped signature
        signature.sign(dsc);

        OutputStream os = new ByteArrayOutputStream();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(purchaseOrder), new StreamResult(os));

        System.out.println(os);
    }

    @Test
    public void marshalTest() {

        AuthnRequest authnRequest = new AuthnRequest()
                                        .issueInstant(LocalDateTime.now())
                                        .version(Version.SAML20)
                                        .protocolBinding(ProtocolBinding.POST)
                                        .assertionConsumerServiceURL("http://sp.com/acs");

        NameID issuer = NameID.of("http://sp.com");

        authnRequest.issuer(issuer);

        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(AuthnRequest.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            try {
//            jaxbMarshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new DefaultNamespacePrefixMapper());
                jaxbMarshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new SAMLNamespaceMapper());
            } catch(PropertyException e) {
                // In case another JAXB implementation is used
                System.err.println("gg");
                e.printStackTrace();
            }

//            XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(System.out);
//            xmlStreamWriter.setPrefix("func", "http://www.func.nl");

            OutputStream os = new ByteArrayOutputStream();
            jaxbMarshaller.marshal(authnRequest, os);

            System.out.println(os.toString());


        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }



}
