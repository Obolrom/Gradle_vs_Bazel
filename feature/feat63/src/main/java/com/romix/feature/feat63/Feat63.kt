package com.romix.feature.feat63

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat63Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat63UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat63FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat63UserSummary
)

data class Feat63UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat63NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat63Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat63Config = Feat63Config()
) {

    fun loadSnapshot(userId: Long): Feat63NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat63NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat63UserSummary {
        return Feat63UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat63FeedItem> {
        val result = java.util.ArrayList<Feat63FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat63FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat63UiMapper {

    fun mapToUi(model: List<Feat63FeedItem>): Feat63UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat63UiModel(
            header = UiText("Feat63 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat63UiModel =
        Feat63UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat63UiModel =
        Feat63UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat63UiModel =
        Feat63UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat63Service(
    private val repository: Feat63Repository,
    private val uiMapper: Feat63UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat63UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat63UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat63UserItem1(val user: CoreUser, val label: String)
data class Feat63UserItem2(val user: CoreUser, val label: String)
data class Feat63UserItem3(val user: CoreUser, val label: String)
data class Feat63UserItem4(val user: CoreUser, val label: String)
data class Feat63UserItem5(val user: CoreUser, val label: String)
data class Feat63UserItem6(val user: CoreUser, val label: String)
data class Feat63UserItem7(val user: CoreUser, val label: String)
data class Feat63UserItem8(val user: CoreUser, val label: String)
data class Feat63UserItem9(val user: CoreUser, val label: String)
data class Feat63UserItem10(val user: CoreUser, val label: String)

data class Feat63StateBlock1(val state: Feat63UiModel, val checksum: Int)
data class Feat63StateBlock2(val state: Feat63UiModel, val checksum: Int)
data class Feat63StateBlock3(val state: Feat63UiModel, val checksum: Int)
data class Feat63StateBlock4(val state: Feat63UiModel, val checksum: Int)
data class Feat63StateBlock5(val state: Feat63UiModel, val checksum: Int)
data class Feat63StateBlock6(val state: Feat63UiModel, val checksum: Int)
data class Feat63StateBlock7(val state: Feat63UiModel, val checksum: Int)
data class Feat63StateBlock8(val state: Feat63UiModel, val checksum: Int)
data class Feat63StateBlock9(val state: Feat63UiModel, val checksum: Int)
data class Feat63StateBlock10(val state: Feat63UiModel, val checksum: Int)

fun buildFeat63UserItem(user: CoreUser, index: Int): Feat63UserItem1 {
    return Feat63UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat63StateBlock(model: Feat63UiModel): Feat63StateBlock1 {
    return Feat63StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat63UserSummary> {
    val list = java.util.ArrayList<Feat63UserSummary>(users.size)
    for (user in users) {
        list += Feat63UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat63UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat63UiModel {
    val summaries = (0 until count).map {
        Feat63UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat63UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat63UiModel> {
    val models = java.util.ArrayList<Feat63UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat63AnalyticsEvent1(val name: String, val value: String)
data class Feat63AnalyticsEvent2(val name: String, val value: String)
data class Feat63AnalyticsEvent3(val name: String, val value: String)
data class Feat63AnalyticsEvent4(val name: String, val value: String)
data class Feat63AnalyticsEvent5(val name: String, val value: String)
data class Feat63AnalyticsEvent6(val name: String, val value: String)
data class Feat63AnalyticsEvent7(val name: String, val value: String)
data class Feat63AnalyticsEvent8(val name: String, val value: String)
data class Feat63AnalyticsEvent9(val name: String, val value: String)
data class Feat63AnalyticsEvent10(val name: String, val value: String)

fun logFeat63Event1(event: Feat63AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat63Event2(event: Feat63AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat63Event3(event: Feat63AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat63Event4(event: Feat63AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat63Event5(event: Feat63AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat63Event6(event: Feat63AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat63Event7(event: Feat63AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat63Event8(event: Feat63AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat63Event9(event: Feat63AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat63Event10(event: Feat63AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat63Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat63Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat63Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat63Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat63Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat63Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat63Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat63Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat63Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat63Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat63(u: CoreUser): Feat63Projection1 =
    Feat63Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat63Projection1> {
    val list = java.util.ArrayList<Feat63Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat63(u)
    }
    return list
}
