package com.romix.feature.feat124

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat124Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat124UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat124FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat124UserSummary
)

data class Feat124UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat124NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat124Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat124Config = Feat124Config()
) {

    fun loadSnapshot(userId: Long): Feat124NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat124NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat124UserSummary {
        return Feat124UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat124FeedItem> {
        val result = java.util.ArrayList<Feat124FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat124FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat124UiMapper {

    fun mapToUi(model: List<Feat124FeedItem>): Feat124UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat124UiModel(
            header = UiText("Feat124 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat124UiModel =
        Feat124UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat124UiModel =
        Feat124UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat124UiModel =
        Feat124UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat124Service(
    private val repository: Feat124Repository,
    private val uiMapper: Feat124UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat124UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat124UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat124UserItem1(val user: CoreUser, val label: String)
data class Feat124UserItem2(val user: CoreUser, val label: String)
data class Feat124UserItem3(val user: CoreUser, val label: String)
data class Feat124UserItem4(val user: CoreUser, val label: String)
data class Feat124UserItem5(val user: CoreUser, val label: String)
data class Feat124UserItem6(val user: CoreUser, val label: String)
data class Feat124UserItem7(val user: CoreUser, val label: String)
data class Feat124UserItem8(val user: CoreUser, val label: String)
data class Feat124UserItem9(val user: CoreUser, val label: String)
data class Feat124UserItem10(val user: CoreUser, val label: String)

data class Feat124StateBlock1(val state: Feat124UiModel, val checksum: Int)
data class Feat124StateBlock2(val state: Feat124UiModel, val checksum: Int)
data class Feat124StateBlock3(val state: Feat124UiModel, val checksum: Int)
data class Feat124StateBlock4(val state: Feat124UiModel, val checksum: Int)
data class Feat124StateBlock5(val state: Feat124UiModel, val checksum: Int)
data class Feat124StateBlock6(val state: Feat124UiModel, val checksum: Int)
data class Feat124StateBlock7(val state: Feat124UiModel, val checksum: Int)
data class Feat124StateBlock8(val state: Feat124UiModel, val checksum: Int)
data class Feat124StateBlock9(val state: Feat124UiModel, val checksum: Int)
data class Feat124StateBlock10(val state: Feat124UiModel, val checksum: Int)

fun buildFeat124UserItem(user: CoreUser, index: Int): Feat124UserItem1 {
    return Feat124UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat124StateBlock(model: Feat124UiModel): Feat124StateBlock1 {
    return Feat124StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat124UserSummary> {
    val list = java.util.ArrayList<Feat124UserSummary>(users.size)
    for (user in users) {
        list += Feat124UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat124UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat124UiModel {
    val summaries = (0 until count).map {
        Feat124UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat124UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat124UiModel> {
    val models = java.util.ArrayList<Feat124UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat124AnalyticsEvent1(val name: String, val value: String)
data class Feat124AnalyticsEvent2(val name: String, val value: String)
data class Feat124AnalyticsEvent3(val name: String, val value: String)
data class Feat124AnalyticsEvent4(val name: String, val value: String)
data class Feat124AnalyticsEvent5(val name: String, val value: String)
data class Feat124AnalyticsEvent6(val name: String, val value: String)
data class Feat124AnalyticsEvent7(val name: String, val value: String)
data class Feat124AnalyticsEvent8(val name: String, val value: String)
data class Feat124AnalyticsEvent9(val name: String, val value: String)
data class Feat124AnalyticsEvent10(val name: String, val value: String)

fun logFeat124Event1(event: Feat124AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat124Event2(event: Feat124AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat124Event3(event: Feat124AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat124Event4(event: Feat124AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat124Event5(event: Feat124AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat124Event6(event: Feat124AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat124Event7(event: Feat124AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat124Event8(event: Feat124AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat124Event9(event: Feat124AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat124Event10(event: Feat124AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat124Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat124Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat124Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat124Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat124Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat124Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat124Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat124Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat124Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat124Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat124(u: CoreUser): Feat124Projection1 =
    Feat124Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat124Projection1> {
    val list = java.util.ArrayList<Feat124Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat124(u)
    }
    return list
}
