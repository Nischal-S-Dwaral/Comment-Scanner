package uos.msc.project.documentation.coverage.comments.scanner.repository;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.*;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Component;
import uos.msc.project.documentation.coverage.comments.scanner.entity.SummaryEntity;
import uos.msc.project.documentation.coverage.comments.scanner.enums.CollectionEnums;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.InternalServerError;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Data/Repository layer implementation for the summary collection
 */
@Component
public class SummaryRepository {

    /**
     * @param summaryEntity document object for the summary collection
     * @return id of the document
     * @throws InternalServerError If failed to add the document to Firestore
     */
    public String add(SummaryEntity summaryEntity) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(CollectionEnums.SUMMARY.getCollection());
        DocumentReference documentReference = databaseReference.document();
        summaryEntity.setId(documentReference.getId());

        ApiFuture<WriteResult> apiFuture = documentReference.create(summaryEntity);
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
     * @return list of summaries for the project
     * @throws InternalServerError If failed to get the document list from Firestore
     */
    public List<SummaryEntity> getSummaryEntityList(String projectId) {
        try {
            Firestore firestore = FirestoreClient.getFirestore();
            CollectionReference databaseReference  = firestore.collection(CollectionEnums.SUMMARY.getCollection());
            ApiFuture<QuerySnapshot> querySnapshotApiFuture = databaseReference.whereEqualTo("projectId", projectId)
                    .orderBy(FieldPath.documentId())
                    .get();
            List<QueryDocumentSnapshot> queryDocumentSnapshotList = querySnapshotApiFuture.get().getDocuments();

            return queryDocumentSnapshotList.stream()
                    .map(data -> data.toObject(SummaryEntity.class))
                    .toList();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get summary list from Firestore for project id- "+projectId+ " :"+exception.getMessage());
        }
    }
}
