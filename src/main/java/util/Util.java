package util;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.*;
import java.security.PrivateKey;
import java.security.Security;

import static java.lang.String.format;

public class Util {

    private File getCertificateFile(String org1UsrAdminCertificatePath){
        File directory = new File(org1UsrAdminCertificatePath);
        File[] matches = directory.listFiles((dir, name) -> name.endsWith(".pem"));
        if (null == matches) {
            throw new RuntimeException(format(
                    "Matches returned null does %s directory exist?", directory
                            .getAbsoluteFile().getName()));
        }
        if (matches.length != 1) {
            throw new RuntimeException(format(
                    "Expected in %s only 1 sk file but found %d", directory
                            .getAbsoluteFile().getName(), matches.length));
        }
        return matches[0];
    }

    private File getPrivateKeyFile(String Org1UsrAdminKeyStorePath) {
        File directory = new File(Org1UsrAdminKeyStorePath);
        File[] matches = directory.listFiles((dir, name) -> name.endsWith("_sk"));

        if (null == matches) {
            throw new RuntimeException(format(
                    "Matches returned null does %s directory exist?", directory
                            .getAbsoluteFile().getName()));
        }
        if (matches.length != 1) {
            throw new RuntimeException(format(
                    "Expected in %s only 1 sk file but found %d", directory
                            .getAbsoluteFile().getName(), matches.length));
        }
        return matches[0];
    }

    public PrivateKey getPrivateKey(String Org1UserAdminKeyStorePath) throws IOException {
        File privateKeyFile = this.getPrivateKeyFile(Org1UserAdminKeyStorePath);
        byte[] data = fileToByteArray(privateKeyFile);
        Reader pemReader = new StringReader(new String(data));

        PrivateKeyInfo pemPair;
        try (PEMParser pemParser = new PEMParser(pemReader)) {
            pemPair = (PrivateKeyInfo) pemParser.readObject();
        }
        Security.addProvider(new BouncyCastleProvider());
        PrivateKey privateKey = new JcaPEMKeyConverter().setProvider(
                BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(pemPair);

        return privateKey;
    }

    public String getCertificate(String Org1UserAdminCertificatePath) throws IOException {
        File certificateFile = this.getCertificateFile(Org1UserAdminCertificatePath);
        return new String(this.fileToByteArray(certificateFile), "UTF-8");
    }
    private byte[] fileToByteArray(File file) throws IOException {
        return IOUtils.toByteArray(new FileInputStream(file));
    }

    public void getAvailableNetworks(String networksFolderPath){
        File directory = new File(networksFolderPath);
        File[] matches = directory.listFiles((dir, name) -> dir.isDirectory());
        System.out.println(matches.length);
    }

}

