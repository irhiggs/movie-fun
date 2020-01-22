package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import java.io.IOException;
import java.util.Optional;

public class S3Store implements BlobStore {

    private final AmazonS3Client amazonS3Client;
    private final String bucketName;

    public S3Store(AmazonS3Client s3Client, String photoStorageBucket) {
        this.bucketName = photoStorageBucket;
        this.amazonS3Client = s3Client;
        if (!amazonS3Client.doesBucketExist(bucketName)) {
            amazonS3Client.createBucket(photoStorageBucket);
        }
    }

    @Override
    public void put(Blob blob) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(blob.contentType);
        objectMetadata.setContentLength(blob.inputStream.available());
        amazonS3Client.putObject(bucketName, blob.name, blob.inputStream, objectMetadata);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        if (amazonS3Client.doesObjectExist(bucketName, name)) {
            S3Object object = amazonS3Client.getObject(bucketName, name);
            return Optional.of(
                new Blob(name, object.getObjectContent(), object.getObjectMetadata().getContentType()));

        }
        return Optional.empty();
    }

    @Override
    public void deleteAll() {
        amazonS3Client.deleteObjects(new DeleteObjectsRequest(bucketName));
    }
}
