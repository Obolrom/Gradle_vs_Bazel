package com.romix.feature.feat329

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat329Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat329UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat329FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat329UserSummary
)

data class Feat329UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat329NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat329Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat329Config = Feat329Config()
) {

    fun loadSnapshot(userId: Long): Feat329NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat329NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat329UserSummary {
        return Feat329UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat329FeedItem> {
        val result = java.util.ArrayList<Feat329FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat329FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat329UiMapper {

    fun mapToUi(model: List<Feat329FeedItem>): Feat329UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat329UiModel(
            header = UiText("Feat329 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat329UiModel =
        Feat329UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat329UiModel =
        Feat329UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat329UiModel =
        Feat329UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat329Service(
    private val repository: Feat329Repository,
    private val uiMapper: Feat329UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat329UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat329UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat329UserItem1(val user: CoreUser, val label: String)
data class Feat329UserItem2(val user: CoreUser, val label: String)
data class Feat329UserItem3(val user: CoreUser, val label: String)
data class Feat329UserItem4(val user: CoreUser, val label: String)
data class Feat329UserItem5(val user: CoreUser, val label: String)
data class Feat329UserItem6(val user: CoreUser, val label: String)
data class Feat329UserItem7(val user: CoreUser, val label: String)
data class Feat329UserItem8(val user: CoreUser, val label: String)
data class Feat329UserItem9(val user: CoreUser, val label: String)
data class Feat329UserItem10(val user: CoreUser, val label: String)

data class Feat329StateBlock1(val state: Feat329UiModel, val checksum: Int)
data class Feat329StateBlock2(val state: Feat329UiModel, val checksum: Int)
data class Feat329StateBlock3(val state: Feat329UiModel, val checksum: Int)
data class Feat329StateBlock4(val state: Feat329UiModel, val checksum: Int)
data class Feat329StateBlock5(val state: Feat329UiModel, val checksum: Int)
data class Feat329StateBlock6(val state: Feat329UiModel, val checksum: Int)
data class Feat329StateBlock7(val state: Feat329UiModel, val checksum: Int)
data class Feat329StateBlock8(val state: Feat329UiModel, val checksum: Int)
data class Feat329StateBlock9(val state: Feat329UiModel, val checksum: Int)
data class Feat329StateBlock10(val state: Feat329UiModel, val checksum: Int)

fun buildFeat329UserItem(user: CoreUser, index: Int): Feat329UserItem1 {
    return Feat329UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat329StateBlock(model: Feat329UiModel): Feat329StateBlock1 {
    return Feat329StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat329UserSummary> {
    val list = java.util.ArrayList<Feat329UserSummary>(users.size)
    for (user in users) {
        list += Feat329UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat329UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat329UiModel {
    val summaries = (0 until count).map {
        Feat329UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat329UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat329UiModel> {
    val models = java.util.ArrayList<Feat329UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat329AnalyticsEvent1(val name: String, val value: String)
data class Feat329AnalyticsEvent2(val name: String, val value: String)
data class Feat329AnalyticsEvent3(val name: String, val value: String)
data class Feat329AnalyticsEvent4(val name: String, val value: String)
data class Feat329AnalyticsEvent5(val name: String, val value: String)
data class Feat329AnalyticsEvent6(val name: String, val value: String)
data class Feat329AnalyticsEvent7(val name: String, val value: String)
data class Feat329AnalyticsEvent8(val name: String, val value: String)
data class Feat329AnalyticsEvent9(val name: String, val value: String)
data class Feat329AnalyticsEvent10(val name: String, val value: String)

fun logFeat329Event1(event: Feat329AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat329Event2(event: Feat329AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat329Event3(event: Feat329AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat329Event4(event: Feat329AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat329Event5(event: Feat329AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat329Event6(event: Feat329AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat329Event7(event: Feat329AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat329Event8(event: Feat329AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat329Event9(event: Feat329AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat329Event10(event: Feat329AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat329Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat329Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat329Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat329Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat329Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat329Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat329Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat329Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat329Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat329Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat329(u: CoreUser): Feat329Projection1 =
    Feat329Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat329Projection1> {
    val list = java.util.ArrayList<Feat329Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat329(u)
    }
    return list
}
