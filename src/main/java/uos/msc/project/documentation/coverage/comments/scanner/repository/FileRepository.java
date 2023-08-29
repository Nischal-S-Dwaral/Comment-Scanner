package uos.msc.project.documentation.coverage.comments.scanner.repository;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.*;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Component;
import uos.msc.project.documentation.coverage.comments.scanner.entity.FileEntity;
import uos.msc.project.documentation.coverage.comments.scanner.enums.CollectionEnums;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.InternalServerError;
import uos.msc.project.documentation.coverage.comments.scanner.utils.JsonUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Data/Repository layer implementation for the file collection
 */
@Component
public class FileRepository {

    /**
     * @param fileEntity document object for the file collection
     * @return the id of the document
     * @throws InternalServerError If failed to add the document to Firestore
     */
    public String add(FileEntity fileEntity) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(CollectionEnums.FILE.getCollection());
        DocumentReference documentReference = databaseReference.document(fileEntity.getId());

        ApiFuture<WriteResult> apiFuture = documentReference.create(fileEntity);
        ApiFutures.addCallback(apiFuture, new ApiFutureCallback<>() {
            @Override
            public void onFailure(Throwable throwable) {
                throw new InternalServerError("Failed to add the document to Firestore: " + throwable.getMessage());
            }

            @Override
            public void onSuccess(WriteResult result) {
                // Document added successfully, you can handle success here
                // You can add any additional logic you want to perform upon success
            }
        }, MoreExecutors.directExecutor());

        return documentReference.getId();
    }

    /**
     * @param projectId id of the project
     * @return list of files for the project
     * @throws InternalServerError If failed to get the document list from Firestore
     */
    public List<FileEntity> getFileEntityList(String projectId) {
        try {
            Firestore firestore = FirestoreClient.getFirestore();
            CollectionReference databaseReference  = firestore.collection(CollectionEnums.FILE.getCollection());
            ApiFuture<QuerySnapshot> querySnapshotApiFuture = databaseReference.whereEqualTo("projectId", projectId)
                    .orderBy(FieldPath.documentId())
                    .get();
            List<QueryDocumentSnapshot> queryDocumentSnapshotList = querySnapshotApiFuture.get().getDocuments();

            return queryDocumentSnapshotList.stream()
                    .map(data -> data.toObject(FileEntity.class))
                    .toList();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get file list from Firestore for project id- "+projectId+ " :"+exception.getMessage());
        }
    }

    public FileEntity getFileEntity(String fileId) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(CollectionEnums.FILE.getCollection());
        try {
            ApiFuture<DocumentSnapshot> documentSnapshotApiFuture = databaseReference.document(fileId).get();
            Map<String, Object> documentMap = documentSnapshotApiFuture.get().getData();
            return JsonUtils.toObject(documentMap, FileEntity.class);
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get ProjectEntity from Firestore: "+exception.getMessage());
        }
    }

    public CompletableFuture<Boolean> deleteDocumentsByProjectIdInBatch(String projectId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Firestore firestore = FirestoreClient.getFirestore();
                CollectionReference collectionReference = firestore.collection(CollectionEnums.FILE.getCollection());

                Query query = collectionReference.whereEqualTo("projectId", projectId);
                ApiFuture<QuerySnapshot> querySnapshot = query.get();

                List<CompletableFuture<Boolean>> deleteFutures = querySnapshot.get().getDocuments()
                        .stream()
                        .map(this::deleteDocumentAsync)
                        .toList();

                CompletableFuture<Void> allOf = CompletableFuture.allOf(deleteFutures.toArray(new CompletableFuture[0]));
                allOf.get();

                return deleteFutures.stream()
                        .allMatch(CompletableFuture::join);

            } catch (ExecutionException | InterruptedException exception) {
                throw new InternalServerError("Failed to delete from Firestore for project id- " + projectId + " :" + exception.getMessage());
            }
        }).thenApply(result -> result);
    }

    private CompletableFuture<Boolean> deleteDocumentAsync(QueryDocumentSnapshot document) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                document.getReference().delete();
                return true;
            } catch (FirestoreException e) {
                return false;
            }
        });
    }
}
