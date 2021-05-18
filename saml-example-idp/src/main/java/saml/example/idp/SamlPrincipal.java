package saml.example.idp;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(of = "nameID")
@Builder(builderMethodName = "hiddenBuilder")
@AllArgsConstructor
@ToString
final class SamlPrincipal implements Principal {

    private String serviceProviderEntityID;
    private String requestID;
    private String assertionConsumerServiceUrl;
    private String relayState;

    @NonNull private List<SamlAttribute> attributes = new ArrayList<>();
    @NonNull private String nameID;
    @NonNull private String nameIDType;

    public SamlPrincipal(String nameID, String nameIDType, List<SamlAttribute> attributes) {
        this.nameID = nameID;
        this.nameIDType = nameIDType;
        this.attributes.addAll(attributes);
    }

    public static SamlPrincipalBuilder builder(String nameID, String nameIDType, List<SamlAttribute> attributes) {
        return hiddenBuilder().nameID(nameID).nameIDType(nameIDType).attributes(attributes);
    }

    @Override
    public String getName() {
        return nameID;
    }
}
