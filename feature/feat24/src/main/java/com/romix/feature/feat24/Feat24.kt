package com.romix.feature.feat24

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat24Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat24UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat24FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat24UserSummary
)

data class Feat24UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat24NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat24Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat24Config = Feat24Config()
) {

    fun loadSnapshot(userId: Long): Feat24NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat24NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat24UserSummary {
        return Feat24UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat24FeedItem> {
        val result = java.util.ArrayList<Feat24FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat24FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat24UiMapper {

    fun mapToUi(model: List<Feat24FeedItem>): Feat24UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat24UiModel(
            header = UiText("Feat24 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat24UiModel =
        Feat24UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat24UiModel =
        Feat24UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat24UiModel =
        Feat24UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat24Service(
    private val repository: Feat24Repository,
    private val uiMapper: Feat24UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat24UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat24UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat24UserItem1(val user: CoreUser, val label: String)
data class Feat24UserItem2(val user: CoreUser, val label: String)
data class Feat24UserItem3(val user: CoreUser, val label: String)
data class Feat24UserItem4(val user: CoreUser, val label: String)
data class Feat24UserItem5(val user: CoreUser, val label: String)
data class Feat24UserItem6(val user: CoreUser, val label: String)
data class Feat24UserItem7(val user: CoreUser, val label: String)
data class Feat24UserItem8(val user: CoreUser, val label: String)
data class Feat24UserItem9(val user: CoreUser, val label: String)
data class Feat24UserItem10(val user: CoreUser, val label: String)

data class Feat24StateBlock1(val state: Feat24UiModel, val checksum: Int)
data class Feat24StateBlock2(val state: Feat24UiModel, val checksum: Int)
data class Feat24StateBlock3(val state: Feat24UiModel, val checksum: Int)
data class Feat24StateBlock4(val state: Feat24UiModel, val checksum: Int)
data class Feat24StateBlock5(val state: Feat24UiModel, val checksum: Int)
data class Feat24StateBlock6(val state: Feat24UiModel, val checksum: Int)
data class Feat24StateBlock7(val state: Feat24UiModel, val checksum: Int)
data class Feat24StateBlock8(val state: Feat24UiModel, val checksum: Int)
data class Feat24StateBlock9(val state: Feat24UiModel, val checksum: Int)
data class Feat24StateBlock10(val state: Feat24UiModel, val checksum: Int)

fun buildFeat24UserItem(user: CoreUser, index: Int): Feat24UserItem1 {
    return Feat24UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat24StateBlock(model: Feat24UiModel): Feat24StateBlock1 {
    return Feat24StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat24UserSummary> {
    val list = java.util.ArrayList<Feat24UserSummary>(users.size)
    for (user in users) {
        list += Feat24UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat24UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat24UiModel {
    val summaries = (0 until count).map {
        Feat24UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat24UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat24UiModel> {
    val models = java.util.ArrayList<Feat24UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat24AnalyticsEvent1(val name: String, val value: String)
data class Feat24AnalyticsEvent2(val name: String, val value: String)
data class Feat24AnalyticsEvent3(val name: String, val value: String)
data class Feat24AnalyticsEvent4(val name: String, val value: String)
data class Feat24AnalyticsEvent5(val name: String, val value: String)
data class Feat24AnalyticsEvent6(val name: String, val value: String)
data class Feat24AnalyticsEvent7(val name: String, val value: String)
data class Feat24AnalyticsEvent8(val name: String, val value: String)
data class Feat24AnalyticsEvent9(val name: String, val value: String)
data class Feat24AnalyticsEvent10(val name: String, val value: String)

fun logFeat24Event1(event: Feat24AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat24Event2(event: Feat24AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat24Event3(event: Feat24AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat24Event4(event: Feat24AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat24Event5(event: Feat24AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat24Event6(event: Feat24AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat24Event7(event: Feat24AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat24Event8(event: Feat24AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat24Event9(event: Feat24AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat24Event10(event: Feat24AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat24Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat24Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat24Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat24Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat24Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat24Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat24Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat24Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat24Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat24Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat24(u: CoreUser): Feat24Projection1 =
    Feat24Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat24Projection1> {
    val list = java.util.ArrayList<Feat24Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat24(u)
    }
    return list
}
