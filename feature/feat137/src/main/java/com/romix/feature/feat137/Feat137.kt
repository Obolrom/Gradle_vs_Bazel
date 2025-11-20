package com.romix.feature.feat137

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat137Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat137UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat137FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat137UserSummary
)

data class Feat137UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat137NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat137Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat137Config = Feat137Config()
) {

    fun loadSnapshot(userId: Long): Feat137NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat137NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat137UserSummary {
        return Feat137UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat137FeedItem> {
        val result = java.util.ArrayList<Feat137FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat137FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat137UiMapper {

    fun mapToUi(model: List<Feat137FeedItem>): Feat137UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat137UiModel(
            header = UiText("Feat137 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat137UiModel =
        Feat137UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat137UiModel =
        Feat137UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat137UiModel =
        Feat137UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat137Service(
    private val repository: Feat137Repository,
    private val uiMapper: Feat137UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat137UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat137UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat137UserItem1(val user: CoreUser, val label: String)
data class Feat137UserItem2(val user: CoreUser, val label: String)
data class Feat137UserItem3(val user: CoreUser, val label: String)
data class Feat137UserItem4(val user: CoreUser, val label: String)
data class Feat137UserItem5(val user: CoreUser, val label: String)
data class Feat137UserItem6(val user: CoreUser, val label: String)
data class Feat137UserItem7(val user: CoreUser, val label: String)
data class Feat137UserItem8(val user: CoreUser, val label: String)
data class Feat137UserItem9(val user: CoreUser, val label: String)
data class Feat137UserItem10(val user: CoreUser, val label: String)

data class Feat137StateBlock1(val state: Feat137UiModel, val checksum: Int)
data class Feat137StateBlock2(val state: Feat137UiModel, val checksum: Int)
data class Feat137StateBlock3(val state: Feat137UiModel, val checksum: Int)
data class Feat137StateBlock4(val state: Feat137UiModel, val checksum: Int)
data class Feat137StateBlock5(val state: Feat137UiModel, val checksum: Int)
data class Feat137StateBlock6(val state: Feat137UiModel, val checksum: Int)
data class Feat137StateBlock7(val state: Feat137UiModel, val checksum: Int)
data class Feat137StateBlock8(val state: Feat137UiModel, val checksum: Int)
data class Feat137StateBlock9(val state: Feat137UiModel, val checksum: Int)
data class Feat137StateBlock10(val state: Feat137UiModel, val checksum: Int)

fun buildFeat137UserItem(user: CoreUser, index: Int): Feat137UserItem1 {
    return Feat137UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat137StateBlock(model: Feat137UiModel): Feat137StateBlock1 {
    return Feat137StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat137UserSummary> {
    val list = java.util.ArrayList<Feat137UserSummary>(users.size)
    for (user in users) {
        list += Feat137UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat137UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat137UiModel {
    val summaries = (0 until count).map {
        Feat137UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat137UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat137UiModel> {
    val models = java.util.ArrayList<Feat137UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat137AnalyticsEvent1(val name: String, val value: String)
data class Feat137AnalyticsEvent2(val name: String, val value: String)
data class Feat137AnalyticsEvent3(val name: String, val value: String)
data class Feat137AnalyticsEvent4(val name: String, val value: String)
data class Feat137AnalyticsEvent5(val name: String, val value: String)
data class Feat137AnalyticsEvent6(val name: String, val value: String)
data class Feat137AnalyticsEvent7(val name: String, val value: String)
data class Feat137AnalyticsEvent8(val name: String, val value: String)
data class Feat137AnalyticsEvent9(val name: String, val value: String)
data class Feat137AnalyticsEvent10(val name: String, val value: String)

fun logFeat137Event1(event: Feat137AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat137Event2(event: Feat137AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat137Event3(event: Feat137AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat137Event4(event: Feat137AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat137Event5(event: Feat137AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat137Event6(event: Feat137AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat137Event7(event: Feat137AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat137Event8(event: Feat137AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat137Event9(event: Feat137AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat137Event10(event: Feat137AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat137Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat137Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat137Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat137Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat137Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat137Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat137Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat137Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat137Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat137Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat137(u: CoreUser): Feat137Projection1 =
    Feat137Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat137Projection1> {
    val list = java.util.ArrayList<Feat137Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat137(u)
    }
    return list
}
