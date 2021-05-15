package saml.example.sp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

final class SamlContextProvider {

    SamlContext getLocalContext(HttpServletRequest request, HttpServletResponse response) {
        return new SamlContext(request, response);
    }
}
