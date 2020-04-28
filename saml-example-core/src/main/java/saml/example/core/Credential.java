package saml.example.core;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@SuppressWarnings("serial")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Credential implements Serializable {

  private String certificate;
  private String key;

}
