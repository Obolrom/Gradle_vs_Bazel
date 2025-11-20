package com.romix.feature.feat356

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat356Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat356UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat356FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat356UserSummary
)

data class Feat356UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat356NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat356Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat356Config = Feat356Config()
) {

    fun loadSnapshot(userId: Long): Feat356NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat356NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat356UserSummary {
        return Feat356UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat356FeedItem> {
        val result = java.util.ArrayList<Feat356FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat356FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat356UiMapper {

    fun mapToUi(model: List<Feat356FeedItem>): Feat356UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat356UiModel(
            header = UiText("Feat356 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat356UiModel =
        Feat356UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat356UiModel =
        Feat356UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat356UiModel =
        Feat356UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat356Service(
    private val repository: Feat356Repository,
    private val uiMapper: Feat356UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat356UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat356UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat356UserItem1(val user: CoreUser, val label: String)
data class Feat356UserItem2(val user: CoreUser, val label: String)
data class Feat356UserItem3(val user: CoreUser, val label: String)
data class Feat356UserItem4(val user: CoreUser, val label: String)
data class Feat356UserItem5(val user: CoreUser, val label: String)
data class Feat356UserItem6(val user: CoreUser, val label: String)
data class Feat356UserItem7(val user: CoreUser, val label: String)
data class Feat356UserItem8(val user: CoreUser, val label: String)
data class Feat356UserItem9(val user: CoreUser, val label: String)
data class Feat356UserItem10(val user: CoreUser, val label: String)

data class Feat356StateBlock1(val state: Feat356UiModel, val checksum: Int)
data class Feat356StateBlock2(val state: Feat356UiModel, val checksum: Int)
data class Feat356StateBlock3(val state: Feat356UiModel, val checksum: Int)
data class Feat356StateBlock4(val state: Feat356UiModel, val checksum: Int)
data class Feat356StateBlock5(val state: Feat356UiModel, val checksum: Int)
data class Feat356StateBlock6(val state: Feat356UiModel, val checksum: Int)
data class Feat356StateBlock7(val state: Feat356UiModel, val checksum: Int)
data class Feat356StateBlock8(val state: Feat356UiModel, val checksum: Int)
data class Feat356StateBlock9(val state: Feat356UiModel, val checksum: Int)
data class Feat356StateBlock10(val state: Feat356UiModel, val checksum: Int)

fun buildFeat356UserItem(user: CoreUser, index: Int): Feat356UserItem1 {
    return Feat356UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat356StateBlock(model: Feat356UiModel): Feat356StateBlock1 {
    return Feat356StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat356UserSummary> {
    val list = java.util.ArrayList<Feat356UserSummary>(users.size)
    for (user in users) {
        list += Feat356UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat356UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat356UiModel {
    val summaries = (0 until count).map {
        Feat356UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat356UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat356UiModel> {
    val models = java.util.ArrayList<Feat356UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat356AnalyticsEvent1(val name: String, val value: String)
data class Feat356AnalyticsEvent2(val name: String, val value: String)
data class Feat356AnalyticsEvent3(val name: String, val value: String)
data class Feat356AnalyticsEvent4(val name: String, val value: String)
data class Feat356AnalyticsEvent5(val name: String, val value: String)
data class Feat356AnalyticsEvent6(val name: String, val value: String)
data class Feat356AnalyticsEvent7(val name: String, val value: String)
data class Feat356AnalyticsEvent8(val name: String, val value: String)
data class Feat356AnalyticsEvent9(val name: String, val value: String)
data class Feat356AnalyticsEvent10(val name: String, val value: String)

fun logFeat356Event1(event: Feat356AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat356Event2(event: Feat356AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat356Event3(event: Feat356AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat356Event4(event: Feat356AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat356Event5(event: Feat356AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat356Event6(event: Feat356AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat356Event7(event: Feat356AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat356Event8(event: Feat356AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat356Event9(event: Feat356AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat356Event10(event: Feat356AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat356Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat356Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat356Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat356Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat356Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat356Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat356Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat356Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat356Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat356Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat356(u: CoreUser): Feat356Projection1 =
    Feat356Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat356Projection1> {
    val list = java.util.ArrayList<Feat356Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat356(u)
    }
    return list
}
