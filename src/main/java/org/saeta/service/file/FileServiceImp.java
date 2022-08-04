package org.saeta.service.file;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
@Service
public class FileServiceImp implements FileService{

    //Path root = Paths.get("/home" ,"sfirma", "Files").toAbsolutePath().normalize();
    Path root = Paths.get("C:\\proyectos\\files");
   

    /**
     * Se crea la carpeta si es que no existe en donde se guardaran los archivos
     */
    @Override
    public void init() {
        try {
            if(!Files.exists(root)){
                Files.createDirectory(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se puede inicializar la carpeta");
        }
    }

    /**
     * Función que guarda el archivo y los guarda en la ruta definida
     * @param file Archivo que se desea guardar
     */
    @Override
    public void save(MultipartFile file) {
        try {
            //copy (que queremos copiar, a donde queremos copiar)
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("No se puede guardar el archivo. Error " + e.getMessage());
        }
    }

    /**
     * Función que retorna un archivo.
     * @param filename Nombre del archivo que se desea obtener
     * @return
     */
    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()){
                return resource;
            }else{
                throw new RuntimeException("No se puede leer el archivo ");
            }
        }catch (MalformedURLException e){
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    /**
     * Función que eliminar todos los archivos
     */
    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    /**
     * Función que permite obtener todos los archivos de la carpeta definida.
     * @return
     */
    @Override
    public Stream<Path> loadAll(){
        try{
            return Files.walk(this.root,1).filter(path -> !path.equals(this.root))
                    .map(this.root::relativize);
        }catch (RuntimeException | IOException e){
            throw new RuntimeException("No se pueden cargar los archivos ");
        }
    }

    /**
     * Función que elimina un archivo.
     * @param filename Nombre del archivo que se desea eliminar
     * @return
     */
    @Override
    public String deleteFile(String filename){
        try {
            Boolean delete = Files.deleteIfExists(this.root.resolve(filename));
            return "Borrado";
        }catch (IOException e){
            e.printStackTrace();
            return "Error Borrando ";
        }
    }
}
