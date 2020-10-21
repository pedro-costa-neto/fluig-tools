package br.com.softdigital.fluig.domains;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Credential
 *
 * @author Pedro Costa
 *
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@NoArgsConstructor
public class Credential {

    private Integer id;
    private String url;
    private String userCode;
    private String userName;
    private String password;
    private Long company;
    private String companyName;
    private boolean environment;

    public Credential(String url, String userCode, String userName, String password, Long company, String companyName, boolean environment) {
        this.url = url;
        this.userCode = userCode;
        this.userName = userName;
        this.password = password;
        this.company = company;
        this.companyName = companyName;
        this.environment = environment;
    }

    public Integer getCompany() {
        return Integer.parseInt(company.toString());
    }

    public String toString() {
        String result = companyName;

        if (id != null) {
            result += " - " + (environment == true ? "Produção" : "Desenvolvimento");
        }

        return result;
    }

}
