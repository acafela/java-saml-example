package saml.example.sp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

final class SamlContext {

    private HttpServletRequest request;
    private HttpServletResponse response;

    SamlContext(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    HttpServletRequest request() {
        return request;
    }

    SamlContext request(HttpServletRequest request) {
        this.request = request;
        return this;
    }

    HttpServletResponse response() {
        return response;
    }

    SamlContext response(HttpServletResponse response) {
        this.response = response;
        return this;
    }
}
