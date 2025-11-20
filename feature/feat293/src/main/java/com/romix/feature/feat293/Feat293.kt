package com.romix.feature.feat293

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat293Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat293UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat293FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat293UserSummary
)

data class Feat293UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat293NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat293Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat293Config = Feat293Config()
) {

    fun loadSnapshot(userId: Long): Feat293NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat293NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat293UserSummary {
        return Feat293UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat293FeedItem> {
        val result = java.util.ArrayList<Feat293FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat293FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat293UiMapper {

    fun mapToUi(model: List<Feat293FeedItem>): Feat293UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat293UiModel(
            header = UiText("Feat293 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat293UiModel =
        Feat293UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat293UiModel =
        Feat293UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat293UiModel =
        Feat293UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat293Service(
    private val repository: Feat293Repository,
    private val uiMapper: Feat293UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat293UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat293UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat293UserItem1(val user: CoreUser, val label: String)
data class Feat293UserItem2(val user: CoreUser, val label: String)
data class Feat293UserItem3(val user: CoreUser, val label: String)
data class Feat293UserItem4(val user: CoreUser, val label: String)
data class Feat293UserItem5(val user: CoreUser, val label: String)
data class Feat293UserItem6(val user: CoreUser, val label: String)
data class Feat293UserItem7(val user: CoreUser, val label: String)
data class Feat293UserItem8(val user: CoreUser, val label: String)
data class Feat293UserItem9(val user: CoreUser, val label: String)
data class Feat293UserItem10(val user: CoreUser, val label: String)

data class Feat293StateBlock1(val state: Feat293UiModel, val checksum: Int)
data class Feat293StateBlock2(val state: Feat293UiModel, val checksum: Int)
data class Feat293StateBlock3(val state: Feat293UiModel, val checksum: Int)
data class Feat293StateBlock4(val state: Feat293UiModel, val checksum: Int)
data class Feat293StateBlock5(val state: Feat293UiModel, val checksum: Int)
data class Feat293StateBlock6(val state: Feat293UiModel, val checksum: Int)
data class Feat293StateBlock7(val state: Feat293UiModel, val checksum: Int)
data class Feat293StateBlock8(val state: Feat293UiModel, val checksum: Int)
data class Feat293StateBlock9(val state: Feat293UiModel, val checksum: Int)
data class Feat293StateBlock10(val state: Feat293UiModel, val checksum: Int)

fun buildFeat293UserItem(user: CoreUser, index: Int): Feat293UserItem1 {
    return Feat293UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat293StateBlock(model: Feat293UiModel): Feat293StateBlock1 {
    return Feat293StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat293UserSummary> {
    val list = java.util.ArrayList<Feat293UserSummary>(users.size)
    for (user in users) {
        list += Feat293UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat293UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat293UiModel {
    val summaries = (0 until count).map {
        Feat293UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat293UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat293UiModel> {
    val models = java.util.ArrayList<Feat293UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat293AnalyticsEvent1(val name: String, val value: String)
data class Feat293AnalyticsEvent2(val name: String, val value: String)
data class Feat293AnalyticsEvent3(val name: String, val value: String)
data class Feat293AnalyticsEvent4(val name: String, val value: String)
data class Feat293AnalyticsEvent5(val name: String, val value: String)
data class Feat293AnalyticsEvent6(val name: String, val value: String)
data class Feat293AnalyticsEvent7(val name: String, val value: String)
data class Feat293AnalyticsEvent8(val name: String, val value: String)
data class Feat293AnalyticsEvent9(val name: String, val value: String)
data class Feat293AnalyticsEvent10(val name: String, val value: String)

fun logFeat293Event1(event: Feat293AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat293Event2(event: Feat293AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat293Event3(event: Feat293AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat293Event4(event: Feat293AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat293Event5(event: Feat293AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat293Event6(event: Feat293AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat293Event7(event: Feat293AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat293Event8(event: Feat293AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat293Event9(event: Feat293AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat293Event10(event: Feat293AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat293Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat293Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat293Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat293Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat293Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat293Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat293Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat293Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat293Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat293Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat293(u: CoreUser): Feat293Projection1 =
    Feat293Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat293Projection1> {
    val list = java.util.ArrayList<Feat293Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat293(u)
    }
    return list
}
