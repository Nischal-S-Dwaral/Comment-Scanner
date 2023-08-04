package uos.msc.project.documentation.coverage.comments.scanner.repository;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.*;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Component;
import uos.msc.project.documentation.coverage.comments.scanner.entity.ProjectEntity;
import uos.msc.project.documentation.coverage.comments.scanner.enums.CollectionEnums;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.InternalServerError;
import uos.msc.project.documentation.coverage.comments.scanner.utils.JsonUtils;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Data/Repository layer implementation for the project collection
 */
@Component
public class ProjectRepository {

    /**
     * @param projectEntity document object for the project collection
     * @return the id of the document else null
     * @throws InternalServerError If failed to add the document to Firestore
     */
    public String add(ProjectEntity projectEntity) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(CollectionEnums.PROJECT.getCollection());
        DocumentReference documentReference = databaseReference.document();
        projectEntity.setId(documentReference.getId());

        ApiFuture<WriteResult> apiFuture = documentReference.create(projectEntity);
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
     * @param userId ID of the user
     * @return list of projects for the user
     */
    public ApiFuture<QuerySnapshot> getByUserId(String userId) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(CollectionEnums.PROJECT.getCollection());
        return databaseReference.whereEqualTo("userId", userId)
                .orderBy(FieldPath.documentId())
                .get();
    }

    /**
     * @param id ID of the project
     * @return project entity associated with the ID
     */
    public ProjectEntity findById(String id) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(CollectionEnums.PROJECT.getCollection());
        try {
            ApiFuture<DocumentSnapshot> documentSnapshotApiFuture = databaseReference.document(id).get();
            Map<String, Object> documentMap = documentSnapshotApiFuture.get().getData();
            return JsonUtils.toObject(documentMap, ProjectEntity.class);
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get ProjectEntity from Firestore: "+exception.getMessage());
        }
    }
}
