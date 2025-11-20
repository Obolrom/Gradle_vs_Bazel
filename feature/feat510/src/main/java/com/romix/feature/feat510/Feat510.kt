package com.romix.feature.feat510

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat510Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat510UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat510FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat510UserSummary
)

data class Feat510UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat510NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat510Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat510Config = Feat510Config()
) {

    fun loadSnapshot(userId: Long): Feat510NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat510NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat510UserSummary {
        return Feat510UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat510FeedItem> {
        val result = java.util.ArrayList<Feat510FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat510FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat510UiMapper {

    fun mapToUi(model: List<Feat510FeedItem>): Feat510UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat510UiModel(
            header = UiText("Feat510 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat510UiModel =
        Feat510UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat510UiModel =
        Feat510UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat510UiModel =
        Feat510UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat510Service(
    private val repository: Feat510Repository,
    private val uiMapper: Feat510UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat510UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat510UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat510UserItem1(val user: CoreUser, val label: String)
data class Feat510UserItem2(val user: CoreUser, val label: String)
data class Feat510UserItem3(val user: CoreUser, val label: String)
data class Feat510UserItem4(val user: CoreUser, val label: String)
data class Feat510UserItem5(val user: CoreUser, val label: String)
data class Feat510UserItem6(val user: CoreUser, val label: String)
data class Feat510UserItem7(val user: CoreUser, val label: String)
data class Feat510UserItem8(val user: CoreUser, val label: String)
data class Feat510UserItem9(val user: CoreUser, val label: String)
data class Feat510UserItem10(val user: CoreUser, val label: String)

data class Feat510StateBlock1(val state: Feat510UiModel, val checksum: Int)
data class Feat510StateBlock2(val state: Feat510UiModel, val checksum: Int)
data class Feat510StateBlock3(val state: Feat510UiModel, val checksum: Int)
data class Feat510StateBlock4(val state: Feat510UiModel, val checksum: Int)
data class Feat510StateBlock5(val state: Feat510UiModel, val checksum: Int)
data class Feat510StateBlock6(val state: Feat510UiModel, val checksum: Int)
data class Feat510StateBlock7(val state: Feat510UiModel, val checksum: Int)
data class Feat510StateBlock8(val state: Feat510UiModel, val checksum: Int)
data class Feat510StateBlock9(val state: Feat510UiModel, val checksum: Int)
data class Feat510StateBlock10(val state: Feat510UiModel, val checksum: Int)

fun buildFeat510UserItem(user: CoreUser, index: Int): Feat510UserItem1 {
    return Feat510UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat510StateBlock(model: Feat510UiModel): Feat510StateBlock1 {
    return Feat510StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat510UserSummary> {
    val list = java.util.ArrayList<Feat510UserSummary>(users.size)
    for (user in users) {
        list += Feat510UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat510UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat510UiModel {
    val summaries = (0 until count).map {
        Feat510UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat510UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat510UiModel> {
    val models = java.util.ArrayList<Feat510UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat510AnalyticsEvent1(val name: String, val value: String)
data class Feat510AnalyticsEvent2(val name: String, val value: String)
data class Feat510AnalyticsEvent3(val name: String, val value: String)
data class Feat510AnalyticsEvent4(val name: String, val value: String)
data class Feat510AnalyticsEvent5(val name: String, val value: String)
data class Feat510AnalyticsEvent6(val name: String, val value: String)
data class Feat510AnalyticsEvent7(val name: String, val value: String)
data class Feat510AnalyticsEvent8(val name: String, val value: String)
data class Feat510AnalyticsEvent9(val name: String, val value: String)
data class Feat510AnalyticsEvent10(val name: String, val value: String)

fun logFeat510Event1(event: Feat510AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat510Event2(event: Feat510AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat510Event3(event: Feat510AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat510Event4(event: Feat510AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat510Event5(event: Feat510AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat510Event6(event: Feat510AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat510Event7(event: Feat510AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat510Event8(event: Feat510AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat510Event9(event: Feat510AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat510Event10(event: Feat510AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat510Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat510Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat510Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat510Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat510Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat510Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat510Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat510Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat510Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat510Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat510(u: CoreUser): Feat510Projection1 =
    Feat510Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat510Projection1> {
    val list = java.util.ArrayList<Feat510Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat510(u)
    }
    return list
}
