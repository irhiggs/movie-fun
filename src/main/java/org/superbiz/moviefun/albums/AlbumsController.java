package org.superbiz.moviefun.albums;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.blobstore.Blob;
import org.superbiz.moviefun.blobstore.BlobStore;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private final AlbumsBean albumsBean;
    private final BlobStore blobStore;

    public AlbumsController(AlbumsBean albumsBean, BlobStore blobStore) {
        this.albumsBean = albumsBean;
        this.blobStore = blobStore;
    }

    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {
        blobStore.put(new Blob(format("covers/%d", albumId), uploadedFile.getInputStream(), uploadedFile.getContentType()));
        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException, URISyntaxException {
        Optional<Blob> blob = blobStore.get(format("covers/%d", albumId));
        Path coverFilePath;
        String contentType = null;
        byte[] imageBytes;
        if (blob.isPresent()) {
            imageBytes = IOUtils.toByteArray(blob.get().inputStream);
            coverFilePath = getCoverFile(albumId).toPath();
            contentType = blob.get().contentType;
        } else {
            ClassLoader classLoader = AlbumsController.class.getClassLoader();
            coverFilePath = Paths.get(classLoader.getResource("default-cover.jpg").toURI());
            imageBytes = readAllBytes(coverFilePath);
        }

        HttpHeaders headers = createImageHttpHeaders(coverFilePath, imageBytes, contentType);

        return new HttpEntity<>(imageBytes, headers);
    }


    private HttpHeaders createImageHttpHeaders(Path coverFilePath, byte[] imageBytes, String maybeContentType) throws IOException {
        String contentType = maybeContentType == null ? new Tika().detect(coverFilePath) : maybeContentType;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(imageBytes.length);
        return headers;
    }

    private File getCoverFile(@PathVariable long albumId) {
        String coverFileName = format("covers/%d", albumId);
        return new File(coverFileName);
    }
}
