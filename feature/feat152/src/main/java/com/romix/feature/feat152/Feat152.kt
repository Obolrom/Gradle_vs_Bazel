package com.romix.feature.feat152

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat152Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat152UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat152FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat152UserSummary
)

data class Feat152UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat152NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat152Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat152Config = Feat152Config()
) {

    fun loadSnapshot(userId: Long): Feat152NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat152NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat152UserSummary {
        return Feat152UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat152FeedItem> {
        val result = java.util.ArrayList<Feat152FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat152FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat152UiMapper {

    fun mapToUi(model: List<Feat152FeedItem>): Feat152UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat152UiModel(
            header = UiText("Feat152 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat152UiModel =
        Feat152UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat152UiModel =
        Feat152UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat152UiModel =
        Feat152UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat152Service(
    private val repository: Feat152Repository,
    private val uiMapper: Feat152UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat152UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat152UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat152UserItem1(val user: CoreUser, val label: String)
data class Feat152UserItem2(val user: CoreUser, val label: String)
data class Feat152UserItem3(val user: CoreUser, val label: String)
data class Feat152UserItem4(val user: CoreUser, val label: String)
data class Feat152UserItem5(val user: CoreUser, val label: String)
data class Feat152UserItem6(val user: CoreUser, val label: String)
data class Feat152UserItem7(val user: CoreUser, val label: String)
data class Feat152UserItem8(val user: CoreUser, val label: String)
data class Feat152UserItem9(val user: CoreUser, val label: String)
data class Feat152UserItem10(val user: CoreUser, val label: String)

data class Feat152StateBlock1(val state: Feat152UiModel, val checksum: Int)
data class Feat152StateBlock2(val state: Feat152UiModel, val checksum: Int)
data class Feat152StateBlock3(val state: Feat152UiModel, val checksum: Int)
data class Feat152StateBlock4(val state: Feat152UiModel, val checksum: Int)
data class Feat152StateBlock5(val state: Feat152UiModel, val checksum: Int)
data class Feat152StateBlock6(val state: Feat152UiModel, val checksum: Int)
data class Feat152StateBlock7(val state: Feat152UiModel, val checksum: Int)
data class Feat152StateBlock8(val state: Feat152UiModel, val checksum: Int)
data class Feat152StateBlock9(val state: Feat152UiModel, val checksum: Int)
data class Feat152StateBlock10(val state: Feat152UiModel, val checksum: Int)

fun buildFeat152UserItem(user: CoreUser, index: Int): Feat152UserItem1 {
    return Feat152UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat152StateBlock(model: Feat152UiModel): Feat152StateBlock1 {
    return Feat152StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat152UserSummary> {
    val list = java.util.ArrayList<Feat152UserSummary>(users.size)
    for (user in users) {
        list += Feat152UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat152UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat152UiModel {
    val summaries = (0 until count).map {
        Feat152UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat152UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat152UiModel> {
    val models = java.util.ArrayList<Feat152UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat152AnalyticsEvent1(val name: String, val value: String)
data class Feat152AnalyticsEvent2(val name: String, val value: String)
data class Feat152AnalyticsEvent3(val name: String, val value: String)
data class Feat152AnalyticsEvent4(val name: String, val value: String)
data class Feat152AnalyticsEvent5(val name: String, val value: String)
data class Feat152AnalyticsEvent6(val name: String, val value: String)
data class Feat152AnalyticsEvent7(val name: String, val value: String)
data class Feat152AnalyticsEvent8(val name: String, val value: String)
data class Feat152AnalyticsEvent9(val name: String, val value: String)
data class Feat152AnalyticsEvent10(val name: String, val value: String)

fun logFeat152Event1(event: Feat152AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat152Event2(event: Feat152AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat152Event3(event: Feat152AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat152Event4(event: Feat152AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat152Event5(event: Feat152AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat152Event6(event: Feat152AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat152Event7(event: Feat152AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat152Event8(event: Feat152AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat152Event9(event: Feat152AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat152Event10(event: Feat152AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat152Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat152Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat152Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat152Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat152Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat152Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat152Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat152Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat152Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat152Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat152(u: CoreUser): Feat152Projection1 =
    Feat152Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat152Projection1> {
    val list = java.util.ArrayList<Feat152Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat152(u)
    }
    return list
}
