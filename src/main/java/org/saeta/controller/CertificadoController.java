package org.saeta.controller;

import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;

public class CertificadoController {
    SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm_dd-MM-yyyy");


    public String getCN(X509Certificate certificado) {

        String dn = certificado.getSubjectDN().toString();
        String cn = "";

        try {
            LdapName ln = new LdapName(dn);

            for (Rdn rdn : ln.getRdns())
                if (rdn.getType().equalsIgnoreCase("CN")) {
                    cn = rdn.getValue().toString();
                    break;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(cn);
        return cn;
    }

    public static String getNumSerie(X509Certificate certificado) {

        String dn = certificado.getSubjectDN().toString();
        String titulo = "";
        String[] tagserie = new String[]{"SERIALNUMBER"};

        try {
            LdapName ln = new LdapName(dn);

            for (Rdn rdn : ln.getRdns())
                if (rdn.getType().equalsIgnoreCase(tagserie[0])) {
                    titulo = rdn.getValue().toString();
                    break;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return titulo;
    }
}
