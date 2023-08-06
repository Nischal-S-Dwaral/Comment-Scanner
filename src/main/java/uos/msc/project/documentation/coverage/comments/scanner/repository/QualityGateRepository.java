package uos.msc.project.documentation.coverage.comments.scanner.repository;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.*;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Component;
import uos.msc.project.documentation.coverage.comments.scanner.entity.QualityGateEntity;
import uos.msc.project.documentation.coverage.comments.scanner.enums.CollectionEnums;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.InternalServerError;
import uos.msc.project.documentation.coverage.comments.scanner.model.qualitygate.UpdateQualityGateRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Data/Repository layer implementation for the quality collection
 */
@Component
public class QualityGateRepository {

    /**
     * Add the quality gate values to the firestore
     * @param qualityGateEntity quality gate entity to be added to the database
     */
    public void add(QualityGateEntity qualityGateEntity) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference = firestore.collection(CollectionEnums.QUALITY_GATE.getCollection());
        DocumentReference documentReference = databaseReference.document();

        ApiFuture<WriteResult> apiFuture = documentReference.create(qualityGateEntity);
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
    }

    /**
     * Get the quality object associated with the user id
     * @param userId the user id
     * @return the quality object associated with the user
     */
    public QualityGateEntity getQualityGateEntity(String userId) {
        try {
            Firestore firestore = FirestoreClient.getFirestore();
            CollectionReference databaseReference  = firestore.collection(CollectionEnums.QUALITY_GATE.getCollection());
            ApiFuture<QuerySnapshot> querySnapshotApiFuture = databaseReference.whereEqualTo("userId", userId)
                    .orderBy(FieldPath.documentId())
                    .get();
            List<QueryDocumentSnapshot> queryDocumentSnapshotList = querySnapshotApiFuture.get().getDocuments();
            List<QualityGateEntity> qualityGateEntityList = queryDocumentSnapshotList.stream()
                    .map(data -> data.toObject(QualityGateEntity.class))
                    .toList();

            return qualityGateEntityList.get(0);

        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get quality gate from Firestore for user id- "+userId+ " :"+exception.getMessage());
        }
    }

    public String update(UpdateQualityGateRequest updateQualityGateRequest) {

        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference = firestore.collection(CollectionEnums.QUALITY_GATE.getCollection());
        Query query = databaseReference.whereEqualTo("userId", updateQualityGateRequest.getUserId());
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        try {
            QuerySnapshot snapshots = querySnapshot.get();
            if (!snapshots.isEmpty()) {
                DocumentSnapshot documentSnapshot = snapshots.getDocuments().get(0);
                String documentId = documentSnapshot.getId();

                Map<String, Object> updates = new HashMap<>();
                updates.put("qualityGate", updateQualityGateRequest.getUpdatedQualityGate());

                ApiFuture<WriteResult> updateFuture = databaseReference.document(documentId).update(updates);
                ApiFutures.addCallback(updateFuture, new ApiFutureCallback<>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        throw new InternalServerError("Failed to update the document in Firestore: " + throwable.getMessage());
                    }

                    @Override
                    public void onSuccess(WriteResult result) {
                        // Document updated successfully, you can handle success here
                        // You can add any additional logic you want to perform upon success
                    }
                }, MoreExecutors.directExecutor());

                return documentId;

            } else {
                throw new InternalServerError("No document found with the specified userId: " + updateQualityGateRequest.getUserId());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new InternalServerError("Error while fetching document from Firestore: " + e.getMessage());
        }
    }
}
