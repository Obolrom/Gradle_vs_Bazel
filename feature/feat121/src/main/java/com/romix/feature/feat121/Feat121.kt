package com.romix.feature.feat121

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat121Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat121UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat121FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat121UserSummary
)

data class Feat121UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat121NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat121Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat121Config = Feat121Config()
) {

    fun loadSnapshot(userId: Long): Feat121NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat121NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat121UserSummary {
        return Feat121UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat121FeedItem> {
        val result = java.util.ArrayList<Feat121FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat121FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat121UiMapper {

    fun mapToUi(model: List<Feat121FeedItem>): Feat121UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat121UiModel(
            header = UiText("Feat121 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat121UiModel =
        Feat121UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat121UiModel =
        Feat121UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat121UiModel =
        Feat121UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat121Service(
    private val repository: Feat121Repository,
    private val uiMapper: Feat121UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat121UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat121UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat121UserItem1(val user: CoreUser, val label: String)
data class Feat121UserItem2(val user: CoreUser, val label: String)
data class Feat121UserItem3(val user: CoreUser, val label: String)
data class Feat121UserItem4(val user: CoreUser, val label: String)
data class Feat121UserItem5(val user: CoreUser, val label: String)
data class Feat121UserItem6(val user: CoreUser, val label: String)
data class Feat121UserItem7(val user: CoreUser, val label: String)
data class Feat121UserItem8(val user: CoreUser, val label: String)
data class Feat121UserItem9(val user: CoreUser, val label: String)
data class Feat121UserItem10(val user: CoreUser, val label: String)

data class Feat121StateBlock1(val state: Feat121UiModel, val checksum: Int)
data class Feat121StateBlock2(val state: Feat121UiModel, val checksum: Int)
data class Feat121StateBlock3(val state: Feat121UiModel, val checksum: Int)
data class Feat121StateBlock4(val state: Feat121UiModel, val checksum: Int)
data class Feat121StateBlock5(val state: Feat121UiModel, val checksum: Int)
data class Feat121StateBlock6(val state: Feat121UiModel, val checksum: Int)
data class Feat121StateBlock7(val state: Feat121UiModel, val checksum: Int)
data class Feat121StateBlock8(val state: Feat121UiModel, val checksum: Int)
data class Feat121StateBlock9(val state: Feat121UiModel, val checksum: Int)
data class Feat121StateBlock10(val state: Feat121UiModel, val checksum: Int)

fun buildFeat121UserItem(user: CoreUser, index: Int): Feat121UserItem1 {
    return Feat121UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat121StateBlock(model: Feat121UiModel): Feat121StateBlock1 {
    return Feat121StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat121UserSummary> {
    val list = java.util.ArrayList<Feat121UserSummary>(users.size)
    for (user in users) {
        list += Feat121UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat121UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat121UiModel {
    val summaries = (0 until count).map {
        Feat121UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat121UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat121UiModel> {
    val models = java.util.ArrayList<Feat121UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat121AnalyticsEvent1(val name: String, val value: String)
data class Feat121AnalyticsEvent2(val name: String, val value: String)
data class Feat121AnalyticsEvent3(val name: String, val value: String)
data class Feat121AnalyticsEvent4(val name: String, val value: String)
data class Feat121AnalyticsEvent5(val name: String, val value: String)
data class Feat121AnalyticsEvent6(val name: String, val value: String)
data class Feat121AnalyticsEvent7(val name: String, val value: String)
data class Feat121AnalyticsEvent8(val name: String, val value: String)
data class Feat121AnalyticsEvent9(val name: String, val value: String)
data class Feat121AnalyticsEvent10(val name: String, val value: String)

fun logFeat121Event1(event: Feat121AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat121Event2(event: Feat121AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat121Event3(event: Feat121AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat121Event4(event: Feat121AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat121Event5(event: Feat121AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat121Event6(event: Feat121AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat121Event7(event: Feat121AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat121Event8(event: Feat121AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat121Event9(event: Feat121AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat121Event10(event: Feat121AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat121Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat121Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat121Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat121Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat121Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat121Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat121Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat121Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat121Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat121Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat121(u: CoreUser): Feat121Projection1 =
    Feat121Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat121Projection1> {
    val list = java.util.ArrayList<Feat121Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat121(u)
    }
    return list
}
