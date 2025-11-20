package com.romix.feature.feat630

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat630Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat630UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat630FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat630UserSummary
)

data class Feat630UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat630NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat630Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat630Config = Feat630Config()
) {

    fun loadSnapshot(userId: Long): Feat630NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat630NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat630UserSummary {
        return Feat630UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat630FeedItem> {
        val result = java.util.ArrayList<Feat630FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat630FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat630UiMapper {

    fun mapToUi(model: List<Feat630FeedItem>): Feat630UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat630UiModel(
            header = UiText("Feat630 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat630UiModel =
        Feat630UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat630UiModel =
        Feat630UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat630UiModel =
        Feat630UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat630Service(
    private val repository: Feat630Repository,
    private val uiMapper: Feat630UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat630UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat630UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat630UserItem1(val user: CoreUser, val label: String)
data class Feat630UserItem2(val user: CoreUser, val label: String)
data class Feat630UserItem3(val user: CoreUser, val label: String)
data class Feat630UserItem4(val user: CoreUser, val label: String)
data class Feat630UserItem5(val user: CoreUser, val label: String)
data class Feat630UserItem6(val user: CoreUser, val label: String)
data class Feat630UserItem7(val user: CoreUser, val label: String)
data class Feat630UserItem8(val user: CoreUser, val label: String)
data class Feat630UserItem9(val user: CoreUser, val label: String)
data class Feat630UserItem10(val user: CoreUser, val label: String)

data class Feat630StateBlock1(val state: Feat630UiModel, val checksum: Int)
data class Feat630StateBlock2(val state: Feat630UiModel, val checksum: Int)
data class Feat630StateBlock3(val state: Feat630UiModel, val checksum: Int)
data class Feat630StateBlock4(val state: Feat630UiModel, val checksum: Int)
data class Feat630StateBlock5(val state: Feat630UiModel, val checksum: Int)
data class Feat630StateBlock6(val state: Feat630UiModel, val checksum: Int)
data class Feat630StateBlock7(val state: Feat630UiModel, val checksum: Int)
data class Feat630StateBlock8(val state: Feat630UiModel, val checksum: Int)
data class Feat630StateBlock9(val state: Feat630UiModel, val checksum: Int)
data class Feat630StateBlock10(val state: Feat630UiModel, val checksum: Int)

fun buildFeat630UserItem(user: CoreUser, index: Int): Feat630UserItem1 {
    return Feat630UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat630StateBlock(model: Feat630UiModel): Feat630StateBlock1 {
    return Feat630StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat630UserSummary> {
    val list = java.util.ArrayList<Feat630UserSummary>(users.size)
    for (user in users) {
        list += Feat630UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat630UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat630UiModel {
    val summaries = (0 until count).map {
        Feat630UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat630UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat630UiModel> {
    val models = java.util.ArrayList<Feat630UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat630AnalyticsEvent1(val name: String, val value: String)
data class Feat630AnalyticsEvent2(val name: String, val value: String)
data class Feat630AnalyticsEvent3(val name: String, val value: String)
data class Feat630AnalyticsEvent4(val name: String, val value: String)
data class Feat630AnalyticsEvent5(val name: String, val value: String)
data class Feat630AnalyticsEvent6(val name: String, val value: String)
data class Feat630AnalyticsEvent7(val name: String, val value: String)
data class Feat630AnalyticsEvent8(val name: String, val value: String)
data class Feat630AnalyticsEvent9(val name: String, val value: String)
data class Feat630AnalyticsEvent10(val name: String, val value: String)

fun logFeat630Event1(event: Feat630AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat630Event2(event: Feat630AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat630Event3(event: Feat630AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat630Event4(event: Feat630AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat630Event5(event: Feat630AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat630Event6(event: Feat630AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat630Event7(event: Feat630AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat630Event8(event: Feat630AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat630Event9(event: Feat630AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat630Event10(event: Feat630AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat630Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat630Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat630Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat630Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat630Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat630Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat630Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat630Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat630Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat630Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat630(u: CoreUser): Feat630Projection1 =
    Feat630Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat630Projection1> {
    val list = java.util.ArrayList<Feat630Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat630(u)
    }
    return list
}
