package com.romix.feature.feat612

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat612Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat612UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat612FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat612UserSummary
)

data class Feat612UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat612NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat612Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat612Config = Feat612Config()
) {

    fun loadSnapshot(userId: Long): Feat612NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat612NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat612UserSummary {
        return Feat612UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat612FeedItem> {
        val result = java.util.ArrayList<Feat612FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat612FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat612UiMapper {

    fun mapToUi(model: List<Feat612FeedItem>): Feat612UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat612UiModel(
            header = UiText("Feat612 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat612UiModel =
        Feat612UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat612UiModel =
        Feat612UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat612UiModel =
        Feat612UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat612Service(
    private val repository: Feat612Repository,
    private val uiMapper: Feat612UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat612UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat612UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat612UserItem1(val user: CoreUser, val label: String)
data class Feat612UserItem2(val user: CoreUser, val label: String)
data class Feat612UserItem3(val user: CoreUser, val label: String)
data class Feat612UserItem4(val user: CoreUser, val label: String)
data class Feat612UserItem5(val user: CoreUser, val label: String)
data class Feat612UserItem6(val user: CoreUser, val label: String)
data class Feat612UserItem7(val user: CoreUser, val label: String)
data class Feat612UserItem8(val user: CoreUser, val label: String)
data class Feat612UserItem9(val user: CoreUser, val label: String)
data class Feat612UserItem10(val user: CoreUser, val label: String)

data class Feat612StateBlock1(val state: Feat612UiModel, val checksum: Int)
data class Feat612StateBlock2(val state: Feat612UiModel, val checksum: Int)
data class Feat612StateBlock3(val state: Feat612UiModel, val checksum: Int)
data class Feat612StateBlock4(val state: Feat612UiModel, val checksum: Int)
data class Feat612StateBlock5(val state: Feat612UiModel, val checksum: Int)
data class Feat612StateBlock6(val state: Feat612UiModel, val checksum: Int)
data class Feat612StateBlock7(val state: Feat612UiModel, val checksum: Int)
data class Feat612StateBlock8(val state: Feat612UiModel, val checksum: Int)
data class Feat612StateBlock9(val state: Feat612UiModel, val checksum: Int)
data class Feat612StateBlock10(val state: Feat612UiModel, val checksum: Int)

fun buildFeat612UserItem(user: CoreUser, index: Int): Feat612UserItem1 {
    return Feat612UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat612StateBlock(model: Feat612UiModel): Feat612StateBlock1 {
    return Feat612StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat612UserSummary> {
    val list = java.util.ArrayList<Feat612UserSummary>(users.size)
    for (user in users) {
        list += Feat612UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat612UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat612UiModel {
    val summaries = (0 until count).map {
        Feat612UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat612UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat612UiModel> {
    val models = java.util.ArrayList<Feat612UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat612AnalyticsEvent1(val name: String, val value: String)
data class Feat612AnalyticsEvent2(val name: String, val value: String)
data class Feat612AnalyticsEvent3(val name: String, val value: String)
data class Feat612AnalyticsEvent4(val name: String, val value: String)
data class Feat612AnalyticsEvent5(val name: String, val value: String)
data class Feat612AnalyticsEvent6(val name: String, val value: String)
data class Feat612AnalyticsEvent7(val name: String, val value: String)
data class Feat612AnalyticsEvent8(val name: String, val value: String)
data class Feat612AnalyticsEvent9(val name: String, val value: String)
data class Feat612AnalyticsEvent10(val name: String, val value: String)

fun logFeat612Event1(event: Feat612AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat612Event2(event: Feat612AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat612Event3(event: Feat612AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat612Event4(event: Feat612AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat612Event5(event: Feat612AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat612Event6(event: Feat612AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat612Event7(event: Feat612AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat612Event8(event: Feat612AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat612Event9(event: Feat612AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat612Event10(event: Feat612AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat612Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat612Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat612Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat612Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat612Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat612Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat612Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat612Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat612Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat612Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat612(u: CoreUser): Feat612Projection1 =
    Feat612Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat612Projection1> {
    val list = java.util.ArrayList<Feat612Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat612(u)
    }
    return list
}
