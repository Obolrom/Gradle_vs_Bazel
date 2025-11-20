#!/usr/bin/env python3
import argparse
from pathlib import Path

ROOT_DIR = Path(__file__).parent

# path for modules
DEFAULT_ROOT_FEATURE_DIR = ROOT_DIR / "feature"
# base package
CONFIG_SNIPPETS_DIR = ROOT_DIR / "generated_config"
SETTINGS_SNIPPET_FILE = CONFIG_SNIPPETS_DIR / "settings_includes.gradle"
APP_BUILD_GRADLE_SNIPPET_FILE = CONFIG_SNIPPETS_DIR / "app_build.gradle"
APP_BUILD_BAZEL_SNIPPET_FILE = CONFIG_SNIPPETS_DIR / "app_build.bazel"
APP_COMPONENT_IMPORTS_SNIPPET_FILE = CONFIG_SNIPPETS_DIR / "component_imports"
APP_COMPONENT_CODE_SNIPPET_FILE = CONFIG_SNIPPETS_DIR / "component_code"
APP_MAIN_ACTIVITY_CODE_SNIPPET_FILE = CONFIG_SNIPPETS_DIR / "main_activity"
BASE_PACKAGE = "com.romix.feature"

# ===== TEMPLATES =====

BUILD_TEMPLATE = """\
load("@rules_kotlin//kotlin:android.bzl", "kt_android_library")

kt_android_library(
    name = "{bazel_module_name}",
    srcs = glob(["src/main/java/**/*.kt"]),
    # manifest = "src/main/AndroidManifest.xml",
    # resource_files = glob(["src/main/res/**"]),
    deps = [
        "//core/model:core_lib_model",
        "//core/network:core_lib_network",
        "//core/ui:core_lib_ui",

        "@maven//:androidx_activity_activity_ktx",
        "@maven//:androidx_core_core_ktx",
        "@maven//:androidx_lifecycle_lifecycle_runtime_ktx",
        "@maven//:androidx_concurrent_concurrent_futures",
        "@maven//:com_google_guava_listenablefuture",
        "@maven//:com_google_guava_guava",
    ],
    visibility = ["//visibility:public"],
)

"""

GRADLE_TEMPLATE = """\
plugins {{
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}}

android {{
    namespace = "com.romix.feature.{module_name}"
    compileSdk = 36

    defaultConfig {{
        minSdk = 26
    }}

    buildFeatures {{
        viewBinding = true
        // compose = true
    }}

    compileOptions {{
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }}

    kotlinOptions {{
        jvmTarget = "17"
    }}
}}

dependencies {{
    implementation(project(":core:model"))
    implementation(project(":core:ui"))
    implementation(project(":core:network"))

    implementation(libs.androidx.core.ktx)

}}

"""

KOTLIN_TEMPLATE = """\
package __PACKAGE__

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class __PREFIX__Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class __PREFIX__UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class __PREFIX__FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: __PREFIX__UserSummary
)

data class __PREFIX__UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class __PREFIX__NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class __PREFIX__Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: __PREFIX__Config = __PREFIX__Config()
) {

    fun loadSnapshot(userId: Long): __PREFIX__NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return __PREFIX__NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): __PREFIX__UserSummary {
        return __PREFIX__UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<__PREFIX__FeedItem> {
        val result = java.util.ArrayList<__PREFIX__FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += __PREFIX__FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class __PREFIX__UiMapper {

    fun mapToUi(model: List<__PREFIX__FeedItem>): __PREFIX__UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return __PREFIX__UiModel(
            header = UiText("__PREFIX__ Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): __PREFIX__UiModel =
        __PREFIX__UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): __PREFIX__UiModel =
        __PREFIX__UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): __PREFIX__UiModel =
        __PREFIX__UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class __PREFIX__Service(
    private val repository: __PREFIX__Repository,
    private val uiMapper: __PREFIX__UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): __PREFIX__UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): __PREFIX__UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class __PREFIX__UserItem1(val user: CoreUser, val label: String)
data class __PREFIX__UserItem2(val user: CoreUser, val label: String)
data class __PREFIX__UserItem3(val user: CoreUser, val label: String)
data class __PREFIX__UserItem4(val user: CoreUser, val label: String)
data class __PREFIX__UserItem5(val user: CoreUser, val label: String)
data class __PREFIX__UserItem6(val user: CoreUser, val label: String)
data class __PREFIX__UserItem7(val user: CoreUser, val label: String)
data class __PREFIX__UserItem8(val user: CoreUser, val label: String)
data class __PREFIX__UserItem9(val user: CoreUser, val label: String)
data class __PREFIX__UserItem10(val user: CoreUser, val label: String)

data class __PREFIX__StateBlock1(val state: __PREFIX__UiModel, val checksum: Int)
data class __PREFIX__StateBlock2(val state: __PREFIX__UiModel, val checksum: Int)
data class __PREFIX__StateBlock3(val state: __PREFIX__UiModel, val checksum: Int)
data class __PREFIX__StateBlock4(val state: __PREFIX__UiModel, val checksum: Int)
data class __PREFIX__StateBlock5(val state: __PREFIX__UiModel, val checksum: Int)
data class __PREFIX__StateBlock6(val state: __PREFIX__UiModel, val checksum: Int)
data class __PREFIX__StateBlock7(val state: __PREFIX__UiModel, val checksum: Int)
data class __PREFIX__StateBlock8(val state: __PREFIX__UiModel, val checksum: Int)
data class __PREFIX__StateBlock9(val state: __PREFIX__UiModel, val checksum: Int)
data class __PREFIX__StateBlock10(val state: __PREFIX__UiModel, val checksum: Int)

fun build__PREFIX__UserItem(user: CoreUser, index: Int): __PREFIX__UserItem1 {
    return __PREFIX__UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun build__PREFIX__StateBlock(model: __PREFIX__UiModel): __PREFIX__StateBlock1 {
    return __PREFIX__StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<__PREFIX__UserSummary> {
    val list = java.util.ArrayList<__PREFIX__UserSummary>(users.size)
    for (user in users) {
        list += __PREFIX__UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<__PREFIX__UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): __PREFIX__UiModel {
    val summaries = (0 until count).map {
        __PREFIX__UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return __PREFIX__UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<__PREFIX__UiModel> {
    val models = java.util.ArrayList<__PREFIX__UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class __PREFIX__AnalyticsEvent1(val name: String, val value: String)
data class __PREFIX__AnalyticsEvent2(val name: String, val value: String)
data class __PREFIX__AnalyticsEvent3(val name: String, val value: String)
data class __PREFIX__AnalyticsEvent4(val name: String, val value: String)
data class __PREFIX__AnalyticsEvent5(val name: String, val value: String)
data class __PREFIX__AnalyticsEvent6(val name: String, val value: String)
data class __PREFIX__AnalyticsEvent7(val name: String, val value: String)
data class __PREFIX__AnalyticsEvent8(val name: String, val value: String)
data class __PREFIX__AnalyticsEvent9(val name: String, val value: String)
data class __PREFIX__AnalyticsEvent10(val name: String, val value: String)

fun log__PREFIX__Event1(event: __PREFIX__AnalyticsEvent1): String = "${event.name}:${event.value}"
fun log__PREFIX__Event2(event: __PREFIX__AnalyticsEvent2): String = "${event.name}:${event.value}"
fun log__PREFIX__Event3(event: __PREFIX__AnalyticsEvent3): String = "${event.name}:${event.value}"
fun log__PREFIX__Event4(event: __PREFIX__AnalyticsEvent4): String = "${event.name}:${event.value}"
fun log__PREFIX__Event5(event: __PREFIX__AnalyticsEvent5): String = "${event.name}:${event.value}"
fun log__PREFIX__Event6(event: __PREFIX__AnalyticsEvent6): String = "${event.name}:${event.value}"
fun log__PREFIX__Event7(event: __PREFIX__AnalyticsEvent7): String = "${event.name}:${event.value}"
fun log__PREFIX__Event8(event: __PREFIX__AnalyticsEvent8): String = "${event.name}:${event.value}"
fun log__PREFIX__Event9(event: __PREFIX__AnalyticsEvent9): String = "${event.name}:${event.value}"
fun log__PREFIX__Event10(event: __PREFIX__AnalyticsEvent10): String = "${event.name}:${event.value}"

data class __PREFIX__Projection1(val id: Long, val label: String, val active: Boolean)
data class __PREFIX__Projection2(val id: Long, val label: String, val active: Boolean)
data class __PREFIX__Projection3(val id: Long, val label: String, val active: Boolean)
data class __PREFIX__Projection4(val id: Long, val label: String, val active: Boolean)
data class __PREFIX__Projection5(val id: Long, val label: String, val active: Boolean)
data class __PREFIX__Projection6(val id: Long, val label: String, val active: Boolean)
data class __PREFIX__Projection7(val id: Long, val label: String, val active: Boolean)
data class __PREFIX__Projection8(val id: Long, val label: String, val active: Boolean)
data class __PREFIX__Projection9(val id: Long, val label: String, val active: Boolean)
data class __PREFIX__Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserTo__PREFIX__(u: CoreUser): __PREFIX__Projection1 =
    __PREFIX__Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<__PREFIX__Projection1> {
    val list = java.util.ArrayList<__PREFIX__Projection1>(users.size)
    for (u in users) {
        list += projectUserTo__PREFIX__(u)
    }
    return list
}
"""


def render_template(template: str, **kwargs) -> str:
    return template.format(**kwargs)


def create_feature_module(root_feature_dir: Path, index: int) -> None:
    module_name = f"feat{index}"
    class_name = f"Feat{index}"
    class_prefix = f"Feat{index}"
    package_name = f"{BASE_PACKAGE}.{module_name}"
    bazel_module_name = f"feature_lib_{index}"

    module_dir = root_feature_dir / module_name
    kotlin_dir = module_dir / "src" / "main" / "java" / package_name.replace(".", "/")

    # create directory
    kotlin_dir.mkdir(parents=True, exist_ok=True)

    # path to files
    build_file = module_dir / "BUILD.bazel"
    gradle_file = module_dir / "build.gradle.kts"
    kotlin_file = kotlin_dir / f"{class_name}.kt"

    # file content generation
    build_content = render_template(
        BUILD_TEMPLATE,
        module_name=module_name,
        package_name=package_name,
        class_name=class_name,
        bazel_module_name=bazel_module_name,
    )
    gradle_content = render_template(
        GRADLE_TEMPLATE,
        module_name=module_name,
        package_name=package_name,
        class_name=class_name,
    )
    kotlin_content = (
        KOTLIN_TEMPLATE
        .replace("__PACKAGE__", package_name)
        .replace("__PREFIX__", class_prefix)
    )

    build_file.write_text(build_content, encoding="utf-8")
    gradle_file.write_text(gradle_content, encoding="utf-8")
    kotlin_file.write_text(kotlin_content, encoding="utf-8")

    print(f"Created module: {module_dir}")


def write_settings_snippet(start: int, count: int) -> None:
    CONFIG_SNIPPETS_DIR.mkdir(parents=True, exist_ok=True)
    lines = [
        f'include(":feature:feat{i}")'
        for i in range(start, start + count)
    ]
    SETTINGS_SNIPPET_FILE.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"Wrote settings includes to: {SETTINGS_SNIPPET_FILE}")


def write_app_build_gradle_snippet(start: int, count: int) -> None:
    CONFIG_SNIPPETS_DIR.mkdir(parents=True, exist_ok=True)
    lines = [
        f'implementation(project(":feature:feat{i}"))'
        for i in range(start, start + count)
    ]
    APP_BUILD_GRADLE_SNIPPET_FILE.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"Wrote app build.gradle to: {APP_BUILD_GRADLE_SNIPPET_FILE}")


def write_app_build_bazel_snippet(start: int, count: int) -> None:
    CONFIG_SNIPPETS_DIR.mkdir(parents=True, exist_ok=True)
    lines = [
        f'"//feature/feat{i}:feature_lib_{i}",'
        for i in range(start, start + count)
    ]
    APP_BUILD_BAZEL_SNIPPET_FILE.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"Wrote app BUILD.bazel to: {APP_BUILD_BAZEL_SNIPPET_FILE}")


def write_app_component_imports_snippet(start: int, count: int) -> None:
    CONFIG_SNIPPETS_DIR.mkdir(parents=True, exist_ok=True)
    lines = [
        f'import com.romix.feature.feat{i}.Feat{i}Repository'
        for i in range(start, start + count)
    ]
    APP_COMPONENT_IMPORTS_SNIPPET_FILE.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"Wrote app BUILD.bazel to: {APP_COMPONENT_IMPORTS_SNIPPET_FILE}")


def write_app_component_code_snippet(start: int, count: int) -> None:
    CONFIG_SNIPPETS_DIR.mkdir(parents=True, exist_ok=True)
    lines = [
        f'val feat{i}Config = Feat{i}Repository()'
        for i in range(start, start + count)
    ]
    APP_COMPONENT_CODE_SNIPPET_FILE.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"Wrote app BUILD.bazel to: {APP_COMPONENT_CODE_SNIPPET_FILE}")


def write_main_activity_code_snippet(start: int, count: int) -> None:
    CONFIG_SNIPPETS_DIR.mkdir(parents=True, exist_ok=True)
    lines = [
        f'appComponent.feat{i}Config.loadSnapshot(1)'
        for i in range(start, start + count)
    ]
    APP_MAIN_ACTIVITY_CODE_SNIPPET_FILE.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"Wrote app BUILD.bazel to: {APP_MAIN_ACTIVITY_CODE_SNIPPET_FILE}")


def main():
    parser = argparse.ArgumentParser(
        description="Generate feature/featN modules with Bazel/Gradle/Kotlin files."
    )
    parser.add_argument(
        "start",
        type=int,
        help="Number to start with (3 → feat3, feat4, ...)",
    )
    parser.add_argument(
        "count",
        type=int,
        help="How many modules to create",
    )
    parser.add_argument(
        "--root-feature-dir",
        type=Path,
        default=DEFAULT_ROOT_FEATURE_DIR,
        help="Root directory for modules (by default ./feature)",
    )

    args = parser.parse_args()

    start = args.start
    count = args.count
    root_feature_dir: Path = args.root_feature_dir

    for i in range(start, start + count):
        create_feature_module(root_feature_dir=root_feature_dir, index=i)

    write_settings_snippet(start, count)
    write_app_build_gradle_snippet(start, count)
    write_app_build_bazel_snippet(start, count)
    write_app_component_imports_snippet(start, count)
    write_app_component_code_snippet(start, count)
    write_main_activity_code_snippet(start, count)


if __name__ == "__main__":
    main()
