package com.romix.feature.feat189

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat189Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat189UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat189FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat189UserSummary
)

data class Feat189UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat189NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat189Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat189Config = Feat189Config()
) {

    fun loadSnapshot(userId: Long): Feat189NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat189NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat189UserSummary {
        return Feat189UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat189FeedItem> {
        val result = java.util.ArrayList<Feat189FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat189FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat189UiMapper {

    fun mapToUi(model: List<Feat189FeedItem>): Feat189UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat189UiModel(
            header = UiText("Feat189 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat189UiModel =
        Feat189UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat189UiModel =
        Feat189UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat189UiModel =
        Feat189UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat189Service(
    private val repository: Feat189Repository,
    private val uiMapper: Feat189UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat189UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat189UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat189UserItem1(val user: CoreUser, val label: String)
data class Feat189UserItem2(val user: CoreUser, val label: String)
data class Feat189UserItem3(val user: CoreUser, val label: String)
data class Feat189UserItem4(val user: CoreUser, val label: String)
data class Feat189UserItem5(val user: CoreUser, val label: String)
data class Feat189UserItem6(val user: CoreUser, val label: String)
data class Feat189UserItem7(val user: CoreUser, val label: String)
data class Feat189UserItem8(val user: CoreUser, val label: String)
data class Feat189UserItem9(val user: CoreUser, val label: String)
data class Feat189UserItem10(val user: CoreUser, val label: String)

data class Feat189StateBlock1(val state: Feat189UiModel, val checksum: Int)
data class Feat189StateBlock2(val state: Feat189UiModel, val checksum: Int)
data class Feat189StateBlock3(val state: Feat189UiModel, val checksum: Int)
data class Feat189StateBlock4(val state: Feat189UiModel, val checksum: Int)
data class Feat189StateBlock5(val state: Feat189UiModel, val checksum: Int)
data class Feat189StateBlock6(val state: Feat189UiModel, val checksum: Int)
data class Feat189StateBlock7(val state: Feat189UiModel, val checksum: Int)
data class Feat189StateBlock8(val state: Feat189UiModel, val checksum: Int)
data class Feat189StateBlock9(val state: Feat189UiModel, val checksum: Int)
data class Feat189StateBlock10(val state: Feat189UiModel, val checksum: Int)

fun buildFeat189UserItem(user: CoreUser, index: Int): Feat189UserItem1 {
    return Feat189UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat189StateBlock(model: Feat189UiModel): Feat189StateBlock1 {
    return Feat189StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat189UserSummary> {
    val list = java.util.ArrayList<Feat189UserSummary>(users.size)
    for (user in users) {
        list += Feat189UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat189UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat189UiModel {
    val summaries = (0 until count).map {
        Feat189UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat189UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat189UiModel> {
    val models = java.util.ArrayList<Feat189UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat189AnalyticsEvent1(val name: String, val value: String)
data class Feat189AnalyticsEvent2(val name: String, val value: String)
data class Feat189AnalyticsEvent3(val name: String, val value: String)
data class Feat189AnalyticsEvent4(val name: String, val value: String)
data class Feat189AnalyticsEvent5(val name: String, val value: String)
data class Feat189AnalyticsEvent6(val name: String, val value: String)
data class Feat189AnalyticsEvent7(val name: String, val value: String)
data class Feat189AnalyticsEvent8(val name: String, val value: String)
data class Feat189AnalyticsEvent9(val name: String, val value: String)
data class Feat189AnalyticsEvent10(val name: String, val value: String)

fun logFeat189Event1(event: Feat189AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat189Event2(event: Feat189AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat189Event3(event: Feat189AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat189Event4(event: Feat189AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat189Event5(event: Feat189AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat189Event6(event: Feat189AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat189Event7(event: Feat189AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat189Event8(event: Feat189AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat189Event9(event: Feat189AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat189Event10(event: Feat189AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat189Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat189Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat189Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat189Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat189Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat189Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat189Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat189Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat189Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat189Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat189(u: CoreUser): Feat189Projection1 =
    Feat189Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat189Projection1> {
    val list = java.util.ArrayList<Feat189Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat189(u)
    }
    return list
}
