package saml.example.sp;

import org.apache.catalina.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import saml.example.core.AuthnRequest;
import saml.example.core.NameID;
import saml.example.core.ProtocolBinding;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import static java.util.Objects.nonNull;

@SpringBootApplication
class SamlSpApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(SamlSpApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SamlSpApplication.class, args);
    }

    @Configuration
    static class AppConfiguration implements WebMvcConfigurer {
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new SsoHandlerInterceptor())
                    .addPathPatterns("/user");
        }
    }

    static class SsoHandlerInterceptor extends HandlerInterceptorAdapter {

        private static final String IDP_URL = "http://localhost:9105";
        private static final String SP_URL = "http://localhost:9106";
        private static final String ACS_PATH = "/acs";

        @Override
        public boolean preHandle(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Object handler) throws Exception {
            HttpSession session = request.getSession(false);
            if (nonNull(session) && (Boolean) session.getAttribute("isAuthenticated")) {
                return true;
            }
            String authnRequest = buildAuthnRequest();
            LOGGER.debug("AuthnRequest[{}]", authnRequest);
            String encodedAuthnRequest = deflateAndBase64Encode(authnRequest);
            LOGGER.debug("Encoded AuthnRequest[{}]", encodedAuthnRequest);
            response.sendRedirect("/test");
            return false;
        }

        private String buildAuthnRequest() {
            return new AuthnRequest().assertionConsumerServiceURL(SP_URL + ACS_PATH)
                                    .protocolBinding(ProtocolBinding.REDIRECT)
                                    .destination(IDP_URL)
                                    .issuer(NameID.of(SP_URL))
                                    .toXml();
        }

        /**
         * Copy of OpenSAML HTTPRedirectDeflateEncoder
         */
        private String deflateAndBase64Encode(String authnRequest) {
            try {
                ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
                Deflater deflater = new Deflater(Deflater.DEFLATED, true);
                DeflaterOutputStream deflateStream = new DeflaterOutputStream(bytesOut, deflater);
                deflateStream.write(authnRequest.getBytes(StandardCharsets.UTF_8));
                deflateStream.finish();
                byte[] base64Bytes = Base64.getEncoder().encode(bytesOut.toByteArray());
                return new String(base64Bytes, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException("Unable to DEFLATE and Base64 encode SAML message", e);
            }
        }
    }

}