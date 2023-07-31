package uos.msc.project.documentation.coverage.comments.scanner.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;

/**
 * Configuration class which will run on startup, used to configure the Firebase connection
 */
@Configuration
@EnableConfigurationProperties
@Slf4j
public class FireBaseConfig{

    /**
     * Bean method to connect to Firebase using the json in the resource folder
     */
    @Bean
    public void createFireBaseApp() {

        try {
            File configFile = ResourceUtils.getFile("classpath:config/comment-scanner-firebase.json");

            FileInputStream serviceAccount =
                    new FileInputStream(configFile);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception exception) {
            log.error("Failed to initialise connection to FireBase: "+exception.getMessage());
        }
    }
}
