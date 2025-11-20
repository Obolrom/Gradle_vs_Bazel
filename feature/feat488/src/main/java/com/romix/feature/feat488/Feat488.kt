package com.romix.feature.feat488

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat488Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat488UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat488FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat488UserSummary
)

data class Feat488UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat488NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat488Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat488Config = Feat488Config()
) {

    fun loadSnapshot(userId: Long): Feat488NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat488NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat488UserSummary {
        return Feat488UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat488FeedItem> {
        val result = java.util.ArrayList<Feat488FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat488FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat488UiMapper {

    fun mapToUi(model: List<Feat488FeedItem>): Feat488UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat488UiModel(
            header = UiText("Feat488 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat488UiModel =
        Feat488UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat488UiModel =
        Feat488UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat488UiModel =
        Feat488UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat488Service(
    private val repository: Feat488Repository,
    private val uiMapper: Feat488UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat488UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat488UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat488UserItem1(val user: CoreUser, val label: String)
data class Feat488UserItem2(val user: CoreUser, val label: String)
data class Feat488UserItem3(val user: CoreUser, val label: String)
data class Feat488UserItem4(val user: CoreUser, val label: String)
data class Feat488UserItem5(val user: CoreUser, val label: String)
data class Feat488UserItem6(val user: CoreUser, val label: String)
data class Feat488UserItem7(val user: CoreUser, val label: String)
data class Feat488UserItem8(val user: CoreUser, val label: String)
data class Feat488UserItem9(val user: CoreUser, val label: String)
data class Feat488UserItem10(val user: CoreUser, val label: String)

data class Feat488StateBlock1(val state: Feat488UiModel, val checksum: Int)
data class Feat488StateBlock2(val state: Feat488UiModel, val checksum: Int)
data class Feat488StateBlock3(val state: Feat488UiModel, val checksum: Int)
data class Feat488StateBlock4(val state: Feat488UiModel, val checksum: Int)
data class Feat488StateBlock5(val state: Feat488UiModel, val checksum: Int)
data class Feat488StateBlock6(val state: Feat488UiModel, val checksum: Int)
data class Feat488StateBlock7(val state: Feat488UiModel, val checksum: Int)
data class Feat488StateBlock8(val state: Feat488UiModel, val checksum: Int)
data class Feat488StateBlock9(val state: Feat488UiModel, val checksum: Int)
data class Feat488StateBlock10(val state: Feat488UiModel, val checksum: Int)

fun buildFeat488UserItem(user: CoreUser, index: Int): Feat488UserItem1 {
    return Feat488UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat488StateBlock(model: Feat488UiModel): Feat488StateBlock1 {
    return Feat488StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat488UserSummary> {
    val list = java.util.ArrayList<Feat488UserSummary>(users.size)
    for (user in users) {
        list += Feat488UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat488UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat488UiModel {
    val summaries = (0 until count).map {
        Feat488UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat488UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat488UiModel> {
    val models = java.util.ArrayList<Feat488UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat488AnalyticsEvent1(val name: String, val value: String)
data class Feat488AnalyticsEvent2(val name: String, val value: String)
data class Feat488AnalyticsEvent3(val name: String, val value: String)
data class Feat488AnalyticsEvent4(val name: String, val value: String)
data class Feat488AnalyticsEvent5(val name: String, val value: String)
data class Feat488AnalyticsEvent6(val name: String, val value: String)
data class Feat488AnalyticsEvent7(val name: String, val value: String)
data class Feat488AnalyticsEvent8(val name: String, val value: String)
data class Feat488AnalyticsEvent9(val name: String, val value: String)
data class Feat488AnalyticsEvent10(val name: String, val value: String)

fun logFeat488Event1(event: Feat488AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat488Event2(event: Feat488AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat488Event3(event: Feat488AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat488Event4(event: Feat488AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat488Event5(event: Feat488AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat488Event6(event: Feat488AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat488Event7(event: Feat488AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat488Event8(event: Feat488AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat488Event9(event: Feat488AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat488Event10(event: Feat488AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat488Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat488Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat488Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat488Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat488Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat488Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat488Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat488Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat488Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat488Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat488(u: CoreUser): Feat488Projection1 =
    Feat488Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat488Projection1> {
    val list = java.util.ArrayList<Feat488Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat488(u)
    }
    return list
}
