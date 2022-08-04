package org.saeta.controller;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.saeta.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@RestController
@CrossOrigin
@RequestMapping("files")
public class FileController {

    private final String rutaorigen="C:/proyectos/files/";
    private final String rutadestino="C:/proyectos/files/firmados/";

    @ResponseBody
    @GetMapping(value = "test")
    public ResponseEntity prueba(){
        HashMap<String, Object> responseMap = new HashMap<>();

        responseMap.put("estado", "Works!");

        return new ResponseEntity(responseMap, HttpStatus.OK);
    }
    @ResponseBody
    @PostMapping(value = "test1/{nombre}")
    public ResponseEntity prueba1(@PathVariable("nombre") String nombre){
      HashMap<String, Object> responseMap = new HashMap<>();
      responseMap.put("estado", "regla");
      responseMap.put("nombre", nombre);
      return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
    }
    @ResponseBody
    @PostMapping(value = "darlett")
    public ResponseEntity prueba2(@RequestParam String nombre){
        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("estado", "regla");
        responseMap.put("nombre", nombre);
        return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
    }
    @Autowired
    FileService fileService;

    @ResponseBody
    @PostMapping(value = "firmadigital")
    public ResponseEntity uploadFile(@RequestParam("file")MultipartFile file, @RequestParam float X, @RequestParam float Y, @RequestParam float ancho, @RequestParam float alto, int numerodepagina, String alias) throws GeneralSecurityException, DocumentException, IOException {
        HashMap<String, Object> responseMap = new HashMap<>();
        try {
            fileService.deleteFile(file.getOriginalFilename());
        } catch (IOException e) {
            System.out.println("No existe el archivo");
        }
        fileService.save(file);
        prueba3(X, Y,  alto,  ancho,  numerodepagina, alias,file.getOriginalFilename() );

        responseMap.put("status", "ok");
        responseMap.put("fileName", file.getOriginalFilename());

        return new ResponseEntity(responseMap,HttpStatus.OK);
    }
    @ResponseBody
    @GetMapping(value = "descargararchivo/{nombredearchivo}")
    public ResponseEntity download(@PathVariable("nombredearchivo")String nombredearchivo){

        Resource file = fileService.load(nombredearchivo);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+file.getFilename() +"\"")
                .body(file);

    }

    public boolean prueba3(float X, float Y, float alto, float ancho, int numerodepagina, String alias, String filenew) throws GeneralSecurityException, IOException, DocumentException {
        CertificadoController certificadocontroller=new CertificadoController();

        Security.addProvider(new BouncyCastleProvider());

        KeyStore ks = KeyStore.getInstance("Windows-MY");
        ks.load(null, null);
        PrivateKey key = (PrivateKey) ks.getKey(alias, null);

        Certificate[] chain = ks.getCertificateChain(alias);

        // Recibimos como parámetro de entrada el nombre del archivo PDF a firmarthi
        FileOutputStream fout = new FileOutputStream(this.rutadestino+filenew);
        PdfReader reader = new PdfReader(this.rutaorigen + filenew);

        PdfStamper stamper = PdfStamper.createSignature(reader, fout, '\u0000', null, true);
        PdfSignatureAppearance sap = stamper.getSignatureAppearance();

        X509Certificate xcert = (X509Certificate) ks.getCertificate(alias);
        String c_name = certificadocontroller.getCN(xcert);
        String textofirma="Firmado digitalmente por: "+c_name+"\n";
        String sdf=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        textofirma=textofirma+"Fecha/Hora: "+sdf;


        sap.setRenderingMode(PdfSignatureAppearance.RenderingMode.DESCRIPTION);
        sap.setLayer2Text(textofirma);


        sap.setVisibleSignature(new Rectangle(
                X, Y, X + ancho,  Y+ alto),numerodepagina, null);

        ExternalSignature pks = new PrivateKeySignature(key, DigestAlgorithms.SHA256, "SunMSCAPI");
        ExternalDigest digest = new BouncyCastleDigest();
        MakeSignature.signDetached(sap, digest, pks, chain,
                null, null, null, 8192 * 2, MakeSignature.CryptoStandard.CMS);

        stamper.close();
        reader.close();
        fout.flush();
        fout.close();
prueba4(filenew);

        return true;
    }

    public void prueba4(String nombredearchivo){
        try{
            File archivoeliminado=new File(rutaorigen+nombredearchivo);
            fileService.deleteFile(File.getOriginalFilename)
        }
        File file=new File(rutadestino+nombredearchivo);
        file.renameTo(new File(rutaorigen+"archivomovido.pdf"));
    }

}
