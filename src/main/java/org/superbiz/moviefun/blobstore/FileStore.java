package org.superbiz.moviefun.blobstore;

import org.apache.tika.Tika;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

@Component
public class FileStore implements BlobStore {

    Tika tika = new Tika();

    @Override
    public void put(Blob blob) throws IOException {
        File targetFile = new File(blob.name);
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            byte[] bytes = new byte[blob.inputStream.available()];
            blob.inputStream.read(bytes);
            outputStream.write(bytes);
        }
        blob.inputStream.close();
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        File targetFile = new File(name);
        if (targetFile.exists()) {
            return Optional.of(new Blob(name, new FileInputStream(targetFile), tika.detect(targetFile)));
        }
        return Optional.empty();
    }

    @Override
    public void deleteAll() {
        try {
            FileUtils.cleanDirectory(new File("covers"));
        } catch (IOException e) {

        }
    }
}
