package org.saeta.service.file;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileService {
    public void init();

    /**
     * Guardar un archivo
     * @param file Archivo que se desea guardar
     */
    public void save(MultipartFile file);

    /**
     * Obtener un archivo
     * @param filename Nombre del archivo que se desea obtener
     * @return
     */
    public Resource load(String filename);

    /**
     * Borrar todos los archivos
     */
    public void deleteAll();

    /**
     * Obtener todos los archivos
     * @return
     */
    public Stream<Path> loadAll();

    /**
     * Eliminar un archivo
     * @param filename
     * @return
     * @throws IOException
     */
    public String deleteFile(String filename) throws IOException;
}
