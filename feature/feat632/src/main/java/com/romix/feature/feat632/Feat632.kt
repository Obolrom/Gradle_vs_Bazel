package com.romix.feature.feat632

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat632Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat632UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat632FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat632UserSummary
)

data class Feat632UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat632NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat632Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat632Config = Feat632Config()
) {

    fun loadSnapshot(userId: Long): Feat632NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat632NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat632UserSummary {
        return Feat632UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat632FeedItem> {
        val result = java.util.ArrayList<Feat632FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat632FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat632UiMapper {

    fun mapToUi(model: List<Feat632FeedItem>): Feat632UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat632UiModel(
            header = UiText("Feat632 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat632UiModel =
        Feat632UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat632UiModel =
        Feat632UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat632UiModel =
        Feat632UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat632Service(
    private val repository: Feat632Repository,
    private val uiMapper: Feat632UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat632UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat632UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat632UserItem1(val user: CoreUser, val label: String)
data class Feat632UserItem2(val user: CoreUser, val label: String)
data class Feat632UserItem3(val user: CoreUser, val label: String)
data class Feat632UserItem4(val user: CoreUser, val label: String)
data class Feat632UserItem5(val user: CoreUser, val label: String)
data class Feat632UserItem6(val user: CoreUser, val label: String)
data class Feat632UserItem7(val user: CoreUser, val label: String)
data class Feat632UserItem8(val user: CoreUser, val label: String)
data class Feat632UserItem9(val user: CoreUser, val label: String)
data class Feat632UserItem10(val user: CoreUser, val label: String)

data class Feat632StateBlock1(val state: Feat632UiModel, val checksum: Int)
data class Feat632StateBlock2(val state: Feat632UiModel, val checksum: Int)
data class Feat632StateBlock3(val state: Feat632UiModel, val checksum: Int)
data class Feat632StateBlock4(val state: Feat632UiModel, val checksum: Int)
data class Feat632StateBlock5(val state: Feat632UiModel, val checksum: Int)
data class Feat632StateBlock6(val state: Feat632UiModel, val checksum: Int)
data class Feat632StateBlock7(val state: Feat632UiModel, val checksum: Int)
data class Feat632StateBlock8(val state: Feat632UiModel, val checksum: Int)
data class Feat632StateBlock9(val state: Feat632UiModel, val checksum: Int)
data class Feat632StateBlock10(val state: Feat632UiModel, val checksum: Int)

fun buildFeat632UserItem(user: CoreUser, index: Int): Feat632UserItem1 {
    return Feat632UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat632StateBlock(model: Feat632UiModel): Feat632StateBlock1 {
    return Feat632StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat632UserSummary> {
    val list = java.util.ArrayList<Feat632UserSummary>(users.size)
    for (user in users) {
        list += Feat632UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat632UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat632UiModel {
    val summaries = (0 until count).map {
        Feat632UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat632UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat632UiModel> {
    val models = java.util.ArrayList<Feat632UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat632AnalyticsEvent1(val name: String, val value: String)
data class Feat632AnalyticsEvent2(val name: String, val value: String)
data class Feat632AnalyticsEvent3(val name: String, val value: String)
data class Feat632AnalyticsEvent4(val name: String, val value: String)
data class Feat632AnalyticsEvent5(val name: String, val value: String)
data class Feat632AnalyticsEvent6(val name: String, val value: String)
data class Feat632AnalyticsEvent7(val name: String, val value: String)
data class Feat632AnalyticsEvent8(val name: String, val value: String)
data class Feat632AnalyticsEvent9(val name: String, val value: String)
data class Feat632AnalyticsEvent10(val name: String, val value: String)

fun logFeat632Event1(event: Feat632AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat632Event2(event: Feat632AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat632Event3(event: Feat632AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat632Event4(event: Feat632AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat632Event5(event: Feat632AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat632Event6(event: Feat632AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat632Event7(event: Feat632AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat632Event8(event: Feat632AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat632Event9(event: Feat632AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat632Event10(event: Feat632AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat632Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat632Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat632Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat632Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat632Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat632Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat632Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat632Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat632Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat632Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat632(u: CoreUser): Feat632Projection1 =
    Feat632Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat632Projection1> {
    val list = java.util.ArrayList<Feat632Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat632(u)
    }
    return list
}
