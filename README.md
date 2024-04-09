<p align="center">
  <img width="96" height="96" src="https://img.icons8.com/pulsar-gradient/96/literature.png" alt="literature"/>
</p>
<p align="center">
    <h1 align="center">ZOLO-BOOKY-FRONTEND</h1>
</p>
<p align="center">
    <em>Book Sharing APP for zolo-booky made using Kotlin.</em>
</p>

<p align="center">
	<img src="https://img.shields.io/github/license/sst-product-team/zolo-booky-frontend?style=flat&color=0080ff" alt="license">
	<img src="https://img.shields.io/github/last-commit/sst-product-team/zolo-booky-frontend?style=flat&logo=git&logoColor=white&color=0080ff" alt="last-commit">
	<img src="https://img.shields.io/github/languages/top/sst-product-team/zolo-booky-frontend?style=flat&color=0080ff" alt="repo-top-language">
	<img src="https://img.shields.io/github/languages/count/sst-product-team/zolo-booky-frontend?style=flat&color=0080ff" alt="repo-language-count">
<p>
<p align="center">
		<em>Developed with the software and tools below.</em>
</p>
<p align="center">
	<img src="https://img.shields.io/badge/Firebase-FFCA28.svg?style=flat&logo=Firebase&logoColor=black" alt="Firebase">
	<img src="https://img.shields.io/badge/Kotlin-7F52FF.svg?style=flat&logo=Kotlin&logoColor=white" alt="Kotlin">
	<img src="https://img.shields.io/badge/Google-4285F4.svg?style=flat&logo=Google&logoColor=white" alt="Google">
	<img src="https://img.shields.io/badge/Android-3DDC84.svg?style=flat&logo=Android&logoColor=white" alt="Android">
	<img src="https://img.shields.io/badge/Facebook-1877F2.svg?style=flat&logo=Facebook&logoColor=white" alt="Facebook">
	<img src="https://img.shields.io/badge/GitHub-181717.svg?style=flat&logo=GitHub&logoColor=white" alt="GitHub">
	<img src="https://img.shields.io/badge/JSON-000000.svg?style=flat&logo=JSON&logoColor=white" alt="JSON">
</p>
<hr>

## 🔗 Quick Links

> - [📍 Overview](#-overview)
> - [📦 Features](#-features)
> - [📂 Repository Structure](#-repository-structure)
> - [🧩 Modules](#-modules)
> - [🚀 Getting Started](#-getting-started)
>   - [⚙️ Installation](#️-installation)
> - [🤝 Contributing](#-contributing)
> - [👏 Acknowledgments](#-acknowledgments)

---

## 📍 Overview

Introducing Zolo Booky, a mobile application designed specifically for bachelor residents in coliving PGs. Developed using Kotlin, it streamlines the process of sharing books among users. With features such as browsing, borrowing, and lending books, Zolo Booky aims to facilitate a sense of community among residents through the exchange of literature. Experience the convenience of organized book management with Zolo Booky.

---

## 📦 Features

- Allows owners to lend books by adding it.
- Allows borrowers to borrow book.
- Sends notification to users on critical events like(someone adds books, someone borrows books, someone returns books, request gets rejected, etc..)
- It uses firebase to send notifications and also creates the userContext;
- Images are stored using blob storage.
- Implemented auto refresh on events like(new books added, books get accepted, etc..)

---

## 📂 Repository Structure

```sh
└── zolo-booky-frontend/
    ├── app
    │   ├── .gitignore
    │   ├── build.gradle.kts
    │   ├── google-services.json
    │   ├── proguard-rules.pro
    │   └── src
    │       ├── androidTest
    │       │   └── java
    │       │       └── com
    │       ├── main
    │       │   ├── AndroidManifest.xml
    │       │   ├── java
    │       │   │   └── com
    │       │   └── res
    │       │       ├── anim
    │       │       ├── drawable
    │       │       ├── layout
    │       │       ├── menu
    │       │       ├── mipmap-anydpi
    │       │       ├── mipmap-hdpi
    │       │       ├── mipmap-mdpi
    │       │       ├── mipmap-xhdpi
    │       │       ├── mipmap-xxhdpi
    │       │       ├── mipmap-xxxhdpi
    │       │       ├── navigation
    │       │       ├── values
    │       │       ├── values-night
    │       │       └── xml
    │       └── test
    │           └── java
    │               └── com
    ├── build.gradle.kts
    ├── google-services.json
    ├── gradle
    │   └── wrapper
    │       ├── gradle-wrapper.jar
    │       └── gradle-wrapper.properties
    ├── gradle.properties
    ├── gradlew
    ├── gradlew.bat
    └── settings.gradle.kts
```

---

## 🧩 Modules

<details closed><summary>.</summary>

| File                                                                                                             |                                           |
| ---                                                                                                              | ---                                              |
| [settings.gradle.kts](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/settings.gradle.kts)   | `settings.gradle.kts`  |
| [google-services.json](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/google-services.json) |`google-services.json` |
| [build.gradle.kts](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/build.gradle.kts)         |  `build.gradle.kts`     |
| [gradlew.bat](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/gradlew.bat)                   |  `gradlew.bat`          |

</details>

<details closed><summary>app</summary>

| File                                                                                                                 |                                               |
| ---                                                                                                                  | ---                                                  |
| [proguard-rules.pro](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/proguard-rules.pro)     |  `app/proguard-rules.pro`   |
| [google-services.json](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/google-services.json) |  `app/google-services.json` |
| [build.gradle.kts](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/build.gradle.kts)         |  `app/build.gradle.kts`     |

</details>

<details closed><summary>app.src.main.java.com.example.test</summary>

| File                                                                                                                                                  |                                                                               |
| ---                                                                                                                                                   | ---                                                                                  |
| [MainActivity.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/MainActivity.kt)             |  `app/src/main/java/com/example/test/MainActivity.kt`       |
| [SplashScreen.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/SplashScreen.kt)             |  `app/src/main/java/com/example/test/SplashScreen.kt`       |
| [HistoryBottomSheet.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/HistoryBottomSheet.kt) |  `app/src/main/java/com/example/test/HistoryBottomSheet.kt` |

</details>

<details closed><summary>app.src.main.java.com.example.test.tabs</summary>

| File                                                                                                                                           |                                                                              |
| ---                                                                                                                                            | ---                                                                                 |
| [TabBorrowed.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/tabs/TabBorrowed.kt)   |  `app/src/main/java/com/example/test/tabs/TabBorrowed.kt`  |
| [TabYourBooks.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/tabs/TabYourBooks.kt) |  `app/src/main/java/com/example/test/tabs/TabYourBooks.kt` |

</details>

<details closed><summary>app.src.main.java.com.example.test.globalContexts</summary>

| File                                                                                                                                               |                                                                                     |
| ---                                                                                                                                                | ---                                                                                        |
| [Constants.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/globalContexts/Constants.kt) |  `app/src/main/java/com/example/test/globalContexts/Constants.kt` |

</details>

<details closed><summary>app.src.main.java.com.example.test.notifications</summary>

| File                                                                                                                                                                  |                                                                                              |
| ---                                                                                                                                                                   | ---                                                                                                 |
| [NotificationService.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/notifications/NotificationService.kt) |  `app/src/main/java/com/example/test/notifications/NotificationService.kt` |

</details>

<details closed><summary>app.src.main.java.com.example.test.activity</summary>

| File                                                                                                                                                                 |                                                                                           |
| ---                                                                                                                                                                  | ---                                                                                              |
| [BookInfoOwnerActivity.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/activity/BookInfoOwnerActivity.kt) |  `app/src/main/java/com/example/test/activity/BookInfoOwnerActivity.kt` |
| [PostBooksActivity.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/activity/PostBooksActivity.kt)         |  `app/src/main/java/com/example/test/activity/PostBooksActivity.kt`     |
| [BookInfoActivity.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/activity/BookInfoActivity.kt)           |  `app/src/main/java/com/example/test/activity/BookInfoActivity.kt`      |

</details>

<details closed><summary>app.src.main.java.com.example.test.entity</summary>

| File                                                                                                                                                         |                                                                                      |
| ---                                                                                                                                                          | ---                                                                                         |
| [UserEntity.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/entity/UserEntity.kt)                 |  `app/src/main/java/com/example/test/entity/UserEntity.kt`         |
| [BooksDetailsEntity.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/entity/BooksDetailsEntity.kt) |  `app/src/main/java/com/example/test/entity/BooksDetailsEntity.kt` |
| [MyBookEntity.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/entity/MyBookEntity.kt)             |  `app/src/main/java/com/example/test/entity/MyBookEntity.kt`       |
| [AppealEntity.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/entity/AppealEntity.kt)             |  `app/src/main/java/com/example/test/entity/AppealEntity.kt`       |
| [ListAppealEntity.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/entity/ListAppealEntity.kt)     |  `app/src/main/java/com/example/test/entity/ListAppealEntity.kt`   |
| [BorrowerEntity.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/entity/BorrowerEntity.kt)         |  `app/src/main/java/com/example/test/entity/BorrowerEntity.kt`     |
| [ListBookEntity.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/entity/ListBookEntity.kt)         |  `app/src/main/java/com/example/test/entity/ListBookEntity.kt`     |

</details>

<details closed><summary>app.src.main.java.com.example.test.fragment</summary>

| File                                                                                                                                                               |                                                                                          |
| ---                                                                                                                                                                | ---                                                                                             |
| [HomeFragment.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/fragment/HomeFragment.kt)                 |  `app/src/main/java/com/example/test/fragment/HomeFragment.kt`         |
| [MyBooksFragment.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/fragment/MyBooksFragment.kt)           |  `app/src/main/java/com/example/test/fragment/MyBooksFragment.kt`      |
| [TransactionsFragment.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/fragment/TransactionsFragment.kt) |  `app/src/main/java/com/example/test/fragment/TransactionsFragment.kt` |

</details>

<details closed><summary>app.src.main.java.com.example.test.adapter</summary>

| File                                                                                                                                                            |                                                                                        |
| ---                                                                                                                                                             | ---                                                                                           |
| [BookBorrowAdapter.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/adapter/BookBorrowAdapter.kt)     |  `app/src/main/java/com/example/test/adapter/BookBorrowAdapter.kt`   |
| [MyBooksAdapter.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/adapter/MyBooksAdapter.kt)           |  `app/src/main/java/com/example/test/adapter/MyBooksAdapter.kt`      |
| [ViewHistoryAdapter.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/adapter/ViewHistoryAdapter.kt)   |  `app/src/main/java/com/example/test/adapter/ViewHistoryAdapter.kt`  |
| [BookListAdapter.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/adapter/BookListAdapter.kt)         |  `app/src/main/java/com/example/test/adapter/BookListAdapter.kt`     |
| [MyRequestsAdapter.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/adapter/MyRequestsAdapter.kt)     |  `app/src/main/java/com/example/test/adapter/MyRequestsAdapter.kt`   |
| [BorrowerListAdapter.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/adapter/BorrowerListAdapter.kt) |  `app/src/main/java/com/example/test/adapter/BorrowerListAdapter.kt` |
| [BookRequestsAdapter.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/adapter/BookRequestsAdapter.kt) |  `app/src/main/java/com/example/test/adapter/BookRequestsAdapter.kt` |
| [TabAdapter.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/main/java/com/example/test/adapter/TabAdapter.kt)                   |  `app/src/main/java/com/example/test/adapter/TabAdapter.kt`          |

</details>

<details closed><summary>app.src.androidTest.java.com.example.test</summary>

| File                                                                                                                                                                   |                                                                                           |
| ---                                                                                                                                                                    | ---                                                                                              |
| [ExampleInstrumentedTest.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/androidTest/java/com/example/test/ExampleInstrumentedTest.kt) |  `app/src/androidTest/java/com/example/test/ExampleInstrumentedTest.kt` |

</details>

<details closed><summary>app.src.test.java.com.example.test</summary>

| File                                                                                                                                            |                                                                            |
| ---                                                                                                                                             | ---                                                                               |
| [ExampleUnitTest.kt](https://github.com/sst-product-team/zolo-booky-frontend/blob/master/app/src/test/java/com/example/test/ExampleUnitTest.kt) |  `app/src/test/java/com/example/test/ExampleUnitTest.kt` |

</details>

---

## 🚀 Getting Started

***Requirements***

Ensure you have the following dependencies installed on your system:

* **Kotlin**: `version 1.x`

### ⚙️ Installation

1. Clone the zolo-booky-frontend repository:

```sh
git clone git@github.com:sst-product-team/zolo-booky-frontend.git
```

2. Change to the project directory:

```sh
cd zolo-booky-frontend
```

3. Install apk into Smartphone/Virtual Device:

```sh
./gradlew installDebug
```

### Building the apk

```sh
./gradlew assembleDebug
```

**Ensure the server is running...Before using the app.**

## 🤝 Contributing

Contributions are welcome! Here are several ways you can contribute:

- **[Submit Pull Requests](https://github.com/sst-product-team/zolo-booky-frontend/blob/main/CONTRIBUTING.md)**: Review open PRs, and submit your own PRs.
- **[Report Issues](https://github.com/sst-product-team/zolo-booky-frontend/issues)**: Submit bugs found or log feature requests for Zolo-booky-frontend.

<details closed>
    <summary>Contributing Guidelines</summary>

1. **Fork the Repository**: Start by forking the project repository to your GitHub account.
2. **Clone Locally**: Clone the forked repository to your local machine using a Git client.
   ```sh
   git clone git@github.com:sst-product-team/zolo-booky-frontend.git
   ```
3. **Create a New Branch**: Always work on a new branch, giving it a descriptive name.
   ```sh
   git checkout -b new-feature-x
   ```
4. **Make Your Changes**: Develop and test your changes locally.
5. **Commit Your Changes**: Commit with a clear message describing your updates.
   ```sh
   git commit -m 'Implemented new feature x.'
   ```
6. **Push to GitHub**: Push the changes to your forked repository.
   ```sh
   git push origin new-feature-x
   ```
7. **Submit a Pull Request**: Create a PR against the original project repository. Clearly describe the changes and their motivations.

Once your PR is reviewed and approved, it will be merged into the main branch.

</details>


## 👏 Acknowledgments

- Team
  - [@cyb3rh4wk](https://github.com/cyb3rh4wk)
  - [@aatmik-panse](https://github.com/aatmik-panse)
  - [@pradyutf](https://github.com/pradyutf)

[**Return**](#-quick-links)

---
