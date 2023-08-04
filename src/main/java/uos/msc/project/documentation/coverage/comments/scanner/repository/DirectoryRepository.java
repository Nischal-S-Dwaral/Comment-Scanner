package uos.msc.project.documentation.coverage.comments.scanner.repository;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.*;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Component;
import uos.msc.project.documentation.coverage.comments.scanner.entity.DirectoryEntity;
import uos.msc.project.documentation.coverage.comments.scanner.enums.CollectionEnums;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.InternalServerError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Data/Repository layer implementation for the directory collection
 */
@Component
public class DirectoryRepository {

    /**
     * @param directoryEntity document object for the directory collection
     * @return the id of the document
     * @throws InternalServerError If failed to add the document to Firestore
     */
    public String add(DirectoryEntity directoryEntity) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(CollectionEnums.DIRECTORY.getCollection());
        DocumentReference documentReference = databaseReference.document(directoryEntity.getId());

        ApiFuture<WriteResult> apiFuture = documentReference.create(directoryEntity);
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
     * @return list of directories for the project
     * @throws InternalServerError If failed to get the document list from Firestore
     */
    public List<DirectoryEntity> getDirectoryEntityList(String projectId) {
        try {
            Firestore firestore = FirestoreClient.getFirestore();
            CollectionReference databaseReference  = firestore.collection(CollectionEnums.DIRECTORY.getCollection());
            ApiFuture<QuerySnapshot> querySnapshotApiFuture = databaseReference.whereEqualTo("projectId", projectId)
                    .orderBy(FieldPath.documentId())
                    .get();
            List<QueryDocumentSnapshot> queryDocumentSnapshotList = querySnapshotApiFuture.get().getDocuments();

            return queryDocumentSnapshotList.stream()
                    .map(data -> data.toObject(DirectoryEntity.class))
                    .toList();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get directory list from Firestore for project id- "+projectId+ " :"+exception.getMessage());
        }
    }

    /**
     * Updating the coverage and if it is the base directory
     * @param directoryEntities list of document object for the directory collection which needs to be updated
     * @return list of updated directories
     * @throws InternalServerError If failed to add the document to Firestore
     */
    public List<String> updateList(List<DirectoryEntity> directoryEntities) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference = firestore.collection(CollectionEnums.DIRECTORY.getCollection());

        WriteBatch batch = firestore.batch();
        List<String> updatedDocumentIds = new ArrayList<>();

        for (DirectoryEntity directoryEntity : directoryEntities) {
            DocumentReference documentReference = databaseReference.document(directoryEntity.getId());
            Map<String, Object> updates = new HashMap<>();
            updates.put("coverage", directoryEntity.getCoverage());
            updates.put("baseDirectory", directoryEntity.isBaseDirectory());

            batch.update(documentReference, updates);
            updatedDocumentIds.add(documentReference.getId());
        }

        ApiFuture<List<WriteResult>> batchFuture = batch.commit();
        ApiFutures.addCallback(batchFuture, new ApiFutureCallback<>() {
            @Override
            public void onFailure(Throwable throwable) {
                throw new InternalServerError("Failed to update the document in Firestore: " + throwable.getMessage());
            }

            @Override
            public void onSuccess(List<WriteResult> results) {
                // Document updated successfully, you can handle success here
                // You can add any additional logic you want to perform upon success
            }
        }, MoreExecutors.directExecutor());

        return updatedDocumentIds;
    }
}
