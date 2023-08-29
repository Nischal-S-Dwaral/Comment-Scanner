package uos.msc.project.documentation.coverage.comments.scanner.repository;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.*;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Component;
import uos.msc.project.documentation.coverage.comments.scanner.entity.CodeLineEntity;
import uos.msc.project.documentation.coverage.comments.scanner.enums.CollectionEnums;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.InternalServerError;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Data/Repository layer implementation for the codeLine collection
 */
@Component
public class CodeLineRepository {

    /**
     * @param codeLineEntityList list of object for the codeLines collection
     * @return list of Ids after batch commit
     * @throws InternalServerError If failed to add the batch of documents to Firestore
     */
    public List<String> addList(List<CodeLineEntity> codeLineEntityList) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference = firestore.collection(CollectionEnums.CODE_LINE.getCollection());
        WriteBatch batch = firestore.batch();

        List<String> documentIds = new ArrayList<>();
        for (CodeLineEntity codeLineEntity : codeLineEntityList) {
            DocumentReference documentReference = databaseReference.document();
            codeLineEntity.setId(documentReference.getId());
            batch.set(documentReference, codeLineEntity);
            documentIds.add(documentReference.getId());
        }
        ApiFuture<List<WriteResult>> apiFuture = batch.commit();

        ApiFutures.addCallback(apiFuture, new ApiFutureCallback<>() {
            @Override
            public void onFailure(Throwable throwable) {
                throw new InternalServerError("Failed to add the documents to Firestore: " + throwable.getMessage());
            }

            @Override
            public void onSuccess(List<WriteResult> results) {
                // Documents added successfully, you can handle success here
                // You can add any additional logic you want to perform upon success
            }
        }, MoreExecutors.directExecutor());

        return documentIds;
    }

    /**
     * @param fileId id of the file
     * @return list of code lines for the file
     * @throws InternalServerError If failed to get the document list from Firestore
     */
    public List<CodeLineEntity> getCodeLines(String fileId) {
        try {
            Firestore firestore = FirestoreClient.getFirestore();
            CollectionReference databaseReference = firestore.collection(CollectionEnums.CODE_LINE.getCollection());
            ApiFuture<QuerySnapshot> querySnapshotApiFuture = databaseReference.whereEqualTo("fileId", fileId)
                    .orderBy(FieldPath.documentId())
                    .get();
            List<QueryDocumentSnapshot> queryDocumentSnapshotList = querySnapshotApiFuture.get().getDocuments();

            return queryDocumentSnapshotList.stream()
                    .map(data -> data.toObject(CodeLineEntity.class))
                    .toList();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get code list from Firestore for file id- "+fileId+ " :"+exception.getMessage());
        }
    }

    public CompletableFuture<Boolean> deleteDocumentsByProjectIdInBatch(String projectId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Firestore firestore = FirestoreClient.getFirestore();
                CollectionReference collectionReference = firestore.collection(CollectionEnums.CODE_LINE.getCollection());

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
                throw new InternalServerError("Failed to delete from Firestore for project id- "+projectId+ " :"+exception.getMessage());
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
