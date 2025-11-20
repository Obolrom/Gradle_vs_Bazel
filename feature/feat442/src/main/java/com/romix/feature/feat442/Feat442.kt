package com.romix.feature.feat442

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat442Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat442UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat442FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat442UserSummary
)

data class Feat442UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat442NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat442Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat442Config = Feat442Config()
) {

    fun loadSnapshot(userId: Long): Feat442NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat442NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat442UserSummary {
        return Feat442UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat442FeedItem> {
        val result = java.util.ArrayList<Feat442FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat442FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat442UiMapper {

    fun mapToUi(model: List<Feat442FeedItem>): Feat442UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat442UiModel(
            header = UiText("Feat442 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat442UiModel =
        Feat442UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat442UiModel =
        Feat442UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat442UiModel =
        Feat442UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat442Service(
    private val repository: Feat442Repository,
    private val uiMapper: Feat442UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat442UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat442UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat442UserItem1(val user: CoreUser, val label: String)
data class Feat442UserItem2(val user: CoreUser, val label: String)
data class Feat442UserItem3(val user: CoreUser, val label: String)
data class Feat442UserItem4(val user: CoreUser, val label: String)
data class Feat442UserItem5(val user: CoreUser, val label: String)
data class Feat442UserItem6(val user: CoreUser, val label: String)
data class Feat442UserItem7(val user: CoreUser, val label: String)
data class Feat442UserItem8(val user: CoreUser, val label: String)
data class Feat442UserItem9(val user: CoreUser, val label: String)
data class Feat442UserItem10(val user: CoreUser, val label: String)

data class Feat442StateBlock1(val state: Feat442UiModel, val checksum: Int)
data class Feat442StateBlock2(val state: Feat442UiModel, val checksum: Int)
data class Feat442StateBlock3(val state: Feat442UiModel, val checksum: Int)
data class Feat442StateBlock4(val state: Feat442UiModel, val checksum: Int)
data class Feat442StateBlock5(val state: Feat442UiModel, val checksum: Int)
data class Feat442StateBlock6(val state: Feat442UiModel, val checksum: Int)
data class Feat442StateBlock7(val state: Feat442UiModel, val checksum: Int)
data class Feat442StateBlock8(val state: Feat442UiModel, val checksum: Int)
data class Feat442StateBlock9(val state: Feat442UiModel, val checksum: Int)
data class Feat442StateBlock10(val state: Feat442UiModel, val checksum: Int)

fun buildFeat442UserItem(user: CoreUser, index: Int): Feat442UserItem1 {
    return Feat442UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat442StateBlock(model: Feat442UiModel): Feat442StateBlock1 {
    return Feat442StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat442UserSummary> {
    val list = java.util.ArrayList<Feat442UserSummary>(users.size)
    for (user in users) {
        list += Feat442UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat442UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat442UiModel {
    val summaries = (0 until count).map {
        Feat442UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat442UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat442UiModel> {
    val models = java.util.ArrayList<Feat442UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat442AnalyticsEvent1(val name: String, val value: String)
data class Feat442AnalyticsEvent2(val name: String, val value: String)
data class Feat442AnalyticsEvent3(val name: String, val value: String)
data class Feat442AnalyticsEvent4(val name: String, val value: String)
data class Feat442AnalyticsEvent5(val name: String, val value: String)
data class Feat442AnalyticsEvent6(val name: String, val value: String)
data class Feat442AnalyticsEvent7(val name: String, val value: String)
data class Feat442AnalyticsEvent8(val name: String, val value: String)
data class Feat442AnalyticsEvent9(val name: String, val value: String)
data class Feat442AnalyticsEvent10(val name: String, val value: String)

fun logFeat442Event1(event: Feat442AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat442Event2(event: Feat442AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat442Event3(event: Feat442AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat442Event4(event: Feat442AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat442Event5(event: Feat442AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat442Event6(event: Feat442AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat442Event7(event: Feat442AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat442Event8(event: Feat442AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat442Event9(event: Feat442AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat442Event10(event: Feat442AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat442Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat442Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat442Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat442Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat442Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat442Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat442Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat442Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat442Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat442Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat442(u: CoreUser): Feat442Projection1 =
    Feat442Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat442Projection1> {
    val list = java.util.ArrayList<Feat442Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat442(u)
    }
    return list
}
