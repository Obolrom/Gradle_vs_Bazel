package com.romix.feature.feat461

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat461Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat461UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat461FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat461UserSummary
)

data class Feat461UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat461NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat461Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat461Config = Feat461Config()
) {

    fun loadSnapshot(userId: Long): Feat461NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat461NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat461UserSummary {
        return Feat461UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat461FeedItem> {
        val result = java.util.ArrayList<Feat461FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat461FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat461UiMapper {

    fun mapToUi(model: List<Feat461FeedItem>): Feat461UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat461UiModel(
            header = UiText("Feat461 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat461UiModel =
        Feat461UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat461UiModel =
        Feat461UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat461UiModel =
        Feat461UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat461Service(
    private val repository: Feat461Repository,
    private val uiMapper: Feat461UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat461UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat461UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat461UserItem1(val user: CoreUser, val label: String)
data class Feat461UserItem2(val user: CoreUser, val label: String)
data class Feat461UserItem3(val user: CoreUser, val label: String)
data class Feat461UserItem4(val user: CoreUser, val label: String)
data class Feat461UserItem5(val user: CoreUser, val label: String)
data class Feat461UserItem6(val user: CoreUser, val label: String)
data class Feat461UserItem7(val user: CoreUser, val label: String)
data class Feat461UserItem8(val user: CoreUser, val label: String)
data class Feat461UserItem9(val user: CoreUser, val label: String)
data class Feat461UserItem10(val user: CoreUser, val label: String)

data class Feat461StateBlock1(val state: Feat461UiModel, val checksum: Int)
data class Feat461StateBlock2(val state: Feat461UiModel, val checksum: Int)
data class Feat461StateBlock3(val state: Feat461UiModel, val checksum: Int)
data class Feat461StateBlock4(val state: Feat461UiModel, val checksum: Int)
data class Feat461StateBlock5(val state: Feat461UiModel, val checksum: Int)
data class Feat461StateBlock6(val state: Feat461UiModel, val checksum: Int)
data class Feat461StateBlock7(val state: Feat461UiModel, val checksum: Int)
data class Feat461StateBlock8(val state: Feat461UiModel, val checksum: Int)
data class Feat461StateBlock9(val state: Feat461UiModel, val checksum: Int)
data class Feat461StateBlock10(val state: Feat461UiModel, val checksum: Int)

fun buildFeat461UserItem(user: CoreUser, index: Int): Feat461UserItem1 {
    return Feat461UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat461StateBlock(model: Feat461UiModel): Feat461StateBlock1 {
    return Feat461StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat461UserSummary> {
    val list = java.util.ArrayList<Feat461UserSummary>(users.size)
    for (user in users) {
        list += Feat461UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat461UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat461UiModel {
    val summaries = (0 until count).map {
        Feat461UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat461UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat461UiModel> {
    val models = java.util.ArrayList<Feat461UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat461AnalyticsEvent1(val name: String, val value: String)
data class Feat461AnalyticsEvent2(val name: String, val value: String)
data class Feat461AnalyticsEvent3(val name: String, val value: String)
data class Feat461AnalyticsEvent4(val name: String, val value: String)
data class Feat461AnalyticsEvent5(val name: String, val value: String)
data class Feat461AnalyticsEvent6(val name: String, val value: String)
data class Feat461AnalyticsEvent7(val name: String, val value: String)
data class Feat461AnalyticsEvent8(val name: String, val value: String)
data class Feat461AnalyticsEvent9(val name: String, val value: String)
data class Feat461AnalyticsEvent10(val name: String, val value: String)

fun logFeat461Event1(event: Feat461AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat461Event2(event: Feat461AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat461Event3(event: Feat461AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat461Event4(event: Feat461AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat461Event5(event: Feat461AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat461Event6(event: Feat461AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat461Event7(event: Feat461AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat461Event8(event: Feat461AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat461Event9(event: Feat461AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat461Event10(event: Feat461AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat461Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat461Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat461Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat461Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat461Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat461Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat461Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat461Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat461Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat461Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat461(u: CoreUser): Feat461Projection1 =
    Feat461Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat461Projection1> {
    val list = java.util.ArrayList<Feat461Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat461(u)
    }
    return list
}
