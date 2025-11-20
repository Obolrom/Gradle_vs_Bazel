package com.romix.feature.feat11

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat11Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat11UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat11FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat11UserSummary
)

data class Feat11UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat11NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat11Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat11Config = Feat11Config()
) {

    fun loadSnapshot(userId: Long): Feat11NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat11NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat11UserSummary {
        return Feat11UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat11FeedItem> {
        val result = java.util.ArrayList<Feat11FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat11FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat11UiMapper {

    fun mapToUi(model: List<Feat11FeedItem>): Feat11UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat11UiModel(
            header = UiText("Feat11 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat11UiModel =
        Feat11UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat11UiModel =
        Feat11UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat11UiModel =
        Feat11UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat11Service(
    private val repository: Feat11Repository,
    private val uiMapper: Feat11UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat11UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat11UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat11UserItem1(val user: CoreUser, val label: String)
data class Feat11UserItem2(val user: CoreUser, val label: String)
data class Feat11UserItem3(val user: CoreUser, val label: String)
data class Feat11UserItem4(val user: CoreUser, val label: String)
data class Feat11UserItem5(val user: CoreUser, val label: String)
data class Feat11UserItem6(val user: CoreUser, val label: String)
data class Feat11UserItem7(val user: CoreUser, val label: String)
data class Feat11UserItem8(val user: CoreUser, val label: String)
data class Feat11UserItem9(val user: CoreUser, val label: String)
data class Feat11UserItem10(val user: CoreUser, val label: String)

data class Feat11StateBlock1(val state: Feat11UiModel, val checksum: Int)
data class Feat11StateBlock2(val state: Feat11UiModel, val checksum: Int)
data class Feat11StateBlock3(val state: Feat11UiModel, val checksum: Int)
data class Feat11StateBlock4(val state: Feat11UiModel, val checksum: Int)
data class Feat11StateBlock5(val state: Feat11UiModel, val checksum: Int)
data class Feat11StateBlock6(val state: Feat11UiModel, val checksum: Int)
data class Feat11StateBlock7(val state: Feat11UiModel, val checksum: Int)
data class Feat11StateBlock8(val state: Feat11UiModel, val checksum: Int)
data class Feat11StateBlock9(val state: Feat11UiModel, val checksum: Int)
data class Feat11StateBlock10(val state: Feat11UiModel, val checksum: Int)

fun buildFeat11UserItem(user: CoreUser, index: Int): Feat11UserItem1 {
    return Feat11UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat11StateBlock(model: Feat11UiModel): Feat11StateBlock1 {
    return Feat11StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat11UserSummary> {
    val list = java.util.ArrayList<Feat11UserSummary>(users.size)
    for (user in users) {
        list += Feat11UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat11UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat11UiModel {
    val summaries = (0 until count).map {
        Feat11UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat11UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat11UiModel> {
    val models = java.util.ArrayList<Feat11UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat11AnalyticsEvent1(val name: String, val value: String)
data class Feat11AnalyticsEvent2(val name: String, val value: String)
data class Feat11AnalyticsEvent3(val name: String, val value: String)
data class Feat11AnalyticsEvent4(val name: String, val value: String)
data class Feat11AnalyticsEvent5(val name: String, val value: String)
data class Feat11AnalyticsEvent6(val name: String, val value: String)
data class Feat11AnalyticsEvent7(val name: String, val value: String)
data class Feat11AnalyticsEvent8(val name: String, val value: String)
data class Feat11AnalyticsEvent9(val name: String, val value: String)
data class Feat11AnalyticsEvent10(val name: String, val value: String)

fun logFeat11Event1(event: Feat11AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat11Event2(event: Feat11AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat11Event3(event: Feat11AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat11Event4(event: Feat11AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat11Event5(event: Feat11AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat11Event6(event: Feat11AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat11Event7(event: Feat11AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat11Event8(event: Feat11AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat11Event9(event: Feat11AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat11Event10(event: Feat11AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat11Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat11Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat11Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat11Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat11Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat11Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat11Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat11Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat11Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat11Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat11(u: CoreUser): Feat11Projection1 =
    Feat11Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat11Projection1> {
    val list = java.util.ArrayList<Feat11Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat11(u)
    }
    return list
}
