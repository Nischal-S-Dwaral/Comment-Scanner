
# Comment Scanner - Backend

The backend server of the Documentation Comments Analysis Platform is implemented using Java 17 and Gradle. This server serves as the computational backbone of the platform and provides key functionalities including code analysis and integration with SCM tools. The front server: [GitHub Link](https://console.firebase.google.com/)

## Table of Contents

- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Key Functionalities](#key-functionalities)
- [Technologies Used](#technologies-used)
- [Contact Information](#contact-information)

## Getting Started

To run this project locally, follow these steps:

1. Install Java 17.
2. Install Gradle.
3. Clone this repository
4. Configure the Firebase service account (see [Configuration](#configuration)).
5. Build the project: `gradle build`
6. Run the application: `gradle bootRun`

## Configuration

Before running the backend, you need to configure the Firebase service account credentials. Follow these steps:

1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Create a new project or select an existing one.
3. Navigate to Project Settings > Service accounts.
4. Generate a new private key for your service account.
5. Save the generated JSON file to the `src/resources/config` directory of your project as `comment-scanner-firebase.json`.


## Key Functionalities

The backend server provides the following key functionalities:

- **Comments Analysis:** Efficiently scans and evaluates documentation comments within the codebase.
- **Integration with SCM Tools:** Interfaces with GitHub through their respective APIs, enabling the platform to integrate external services for code retrieval.

## Technologies Used

- Java 17
- Gradle
- Google Firestore (NoSQL database)

## TODO
- [ ] Extend for other programming languages
- [ ] Retrieve codebase data from GitLab
- [ ] Advance analysis techniques to incorporate natural language processing (NLP)

## Contact Information

- Author: Nischal Srinivas Dwaral
- Email: nischal.dwaral@gmail.com
- GitHub: [https://github.com/Nischal-S-Dwaral](https://github.com/Nischal-S-Dwaral)