package com.romix.feature.feat380

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat380Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat380UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat380FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat380UserSummary
)

data class Feat380UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat380NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat380Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat380Config = Feat380Config()
) {

    fun loadSnapshot(userId: Long): Feat380NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat380NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat380UserSummary {
        return Feat380UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat380FeedItem> {
        val result = java.util.ArrayList<Feat380FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat380FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat380UiMapper {

    fun mapToUi(model: List<Feat380FeedItem>): Feat380UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat380UiModel(
            header = UiText("Feat380 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat380UiModel =
        Feat380UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat380UiModel =
        Feat380UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat380UiModel =
        Feat380UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat380Service(
    private val repository: Feat380Repository,
    private val uiMapper: Feat380UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat380UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat380UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat380UserItem1(val user: CoreUser, val label: String)
data class Feat380UserItem2(val user: CoreUser, val label: String)
data class Feat380UserItem3(val user: CoreUser, val label: String)
data class Feat380UserItem4(val user: CoreUser, val label: String)
data class Feat380UserItem5(val user: CoreUser, val label: String)
data class Feat380UserItem6(val user: CoreUser, val label: String)
data class Feat380UserItem7(val user: CoreUser, val label: String)
data class Feat380UserItem8(val user: CoreUser, val label: String)
data class Feat380UserItem9(val user: CoreUser, val label: String)
data class Feat380UserItem10(val user: CoreUser, val label: String)

data class Feat380StateBlock1(val state: Feat380UiModel, val checksum: Int)
data class Feat380StateBlock2(val state: Feat380UiModel, val checksum: Int)
data class Feat380StateBlock3(val state: Feat380UiModel, val checksum: Int)
data class Feat380StateBlock4(val state: Feat380UiModel, val checksum: Int)
data class Feat380StateBlock5(val state: Feat380UiModel, val checksum: Int)
data class Feat380StateBlock6(val state: Feat380UiModel, val checksum: Int)
data class Feat380StateBlock7(val state: Feat380UiModel, val checksum: Int)
data class Feat380StateBlock8(val state: Feat380UiModel, val checksum: Int)
data class Feat380StateBlock9(val state: Feat380UiModel, val checksum: Int)
data class Feat380StateBlock10(val state: Feat380UiModel, val checksum: Int)

fun buildFeat380UserItem(user: CoreUser, index: Int): Feat380UserItem1 {
    return Feat380UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat380StateBlock(model: Feat380UiModel): Feat380StateBlock1 {
    return Feat380StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat380UserSummary> {
    val list = java.util.ArrayList<Feat380UserSummary>(users.size)
    for (user in users) {
        list += Feat380UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat380UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat380UiModel {
    val summaries = (0 until count).map {
        Feat380UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat380UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat380UiModel> {
    val models = java.util.ArrayList<Feat380UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat380AnalyticsEvent1(val name: String, val value: String)
data class Feat380AnalyticsEvent2(val name: String, val value: String)
data class Feat380AnalyticsEvent3(val name: String, val value: String)
data class Feat380AnalyticsEvent4(val name: String, val value: String)
data class Feat380AnalyticsEvent5(val name: String, val value: String)
data class Feat380AnalyticsEvent6(val name: String, val value: String)
data class Feat380AnalyticsEvent7(val name: String, val value: String)
data class Feat380AnalyticsEvent8(val name: String, val value: String)
data class Feat380AnalyticsEvent9(val name: String, val value: String)
data class Feat380AnalyticsEvent10(val name: String, val value: String)

fun logFeat380Event1(event: Feat380AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat380Event2(event: Feat380AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat380Event3(event: Feat380AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat380Event4(event: Feat380AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat380Event5(event: Feat380AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat380Event6(event: Feat380AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat380Event7(event: Feat380AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat380Event8(event: Feat380AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat380Event9(event: Feat380AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat380Event10(event: Feat380AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat380Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat380Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat380Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat380Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat380Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat380Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat380Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat380Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat380Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat380Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat380(u: CoreUser): Feat380Projection1 =
    Feat380Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat380Projection1> {
    val list = java.util.ArrayList<Feat380Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat380(u)
    }
    return list
}
