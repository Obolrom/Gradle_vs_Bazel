package com.romix.feature.feat416

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat416Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat416UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat416FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat416UserSummary
)

data class Feat416UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat416NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat416Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat416Config = Feat416Config()
) {

    fun loadSnapshot(userId: Long): Feat416NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat416NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat416UserSummary {
        return Feat416UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat416FeedItem> {
        val result = java.util.ArrayList<Feat416FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat416FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat416UiMapper {

    fun mapToUi(model: List<Feat416FeedItem>): Feat416UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat416UiModel(
            header = UiText("Feat416 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat416UiModel =
        Feat416UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat416UiModel =
        Feat416UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat416UiModel =
        Feat416UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat416Service(
    private val repository: Feat416Repository,
    private val uiMapper: Feat416UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat416UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat416UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat416UserItem1(val user: CoreUser, val label: String)
data class Feat416UserItem2(val user: CoreUser, val label: String)
data class Feat416UserItem3(val user: CoreUser, val label: String)
data class Feat416UserItem4(val user: CoreUser, val label: String)
data class Feat416UserItem5(val user: CoreUser, val label: String)
data class Feat416UserItem6(val user: CoreUser, val label: String)
data class Feat416UserItem7(val user: CoreUser, val label: String)
data class Feat416UserItem8(val user: CoreUser, val label: String)
data class Feat416UserItem9(val user: CoreUser, val label: String)
data class Feat416UserItem10(val user: CoreUser, val label: String)

data class Feat416StateBlock1(val state: Feat416UiModel, val checksum: Int)
data class Feat416StateBlock2(val state: Feat416UiModel, val checksum: Int)
data class Feat416StateBlock3(val state: Feat416UiModel, val checksum: Int)
data class Feat416StateBlock4(val state: Feat416UiModel, val checksum: Int)
data class Feat416StateBlock5(val state: Feat416UiModel, val checksum: Int)
data class Feat416StateBlock6(val state: Feat416UiModel, val checksum: Int)
data class Feat416StateBlock7(val state: Feat416UiModel, val checksum: Int)
data class Feat416StateBlock8(val state: Feat416UiModel, val checksum: Int)
data class Feat416StateBlock9(val state: Feat416UiModel, val checksum: Int)
data class Feat416StateBlock10(val state: Feat416UiModel, val checksum: Int)

fun buildFeat416UserItem(user: CoreUser, index: Int): Feat416UserItem1 {
    return Feat416UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat416StateBlock(model: Feat416UiModel): Feat416StateBlock1 {
    return Feat416StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat416UserSummary> {
    val list = java.util.ArrayList<Feat416UserSummary>(users.size)
    for (user in users) {
        list += Feat416UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat416UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat416UiModel {
    val summaries = (0 until count).map {
        Feat416UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat416UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat416UiModel> {
    val models = java.util.ArrayList<Feat416UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat416AnalyticsEvent1(val name: String, val value: String)
data class Feat416AnalyticsEvent2(val name: String, val value: String)
data class Feat416AnalyticsEvent3(val name: String, val value: String)
data class Feat416AnalyticsEvent4(val name: String, val value: String)
data class Feat416AnalyticsEvent5(val name: String, val value: String)
data class Feat416AnalyticsEvent6(val name: String, val value: String)
data class Feat416AnalyticsEvent7(val name: String, val value: String)
data class Feat416AnalyticsEvent8(val name: String, val value: String)
data class Feat416AnalyticsEvent9(val name: String, val value: String)
data class Feat416AnalyticsEvent10(val name: String, val value: String)

fun logFeat416Event1(event: Feat416AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat416Event2(event: Feat416AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat416Event3(event: Feat416AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat416Event4(event: Feat416AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat416Event5(event: Feat416AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat416Event6(event: Feat416AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat416Event7(event: Feat416AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat416Event8(event: Feat416AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat416Event9(event: Feat416AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat416Event10(event: Feat416AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat416Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat416Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat416Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat416Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat416Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat416Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat416Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat416Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat416Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat416Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat416(u: CoreUser): Feat416Projection1 =
    Feat416Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat416Projection1> {
    val list = java.util.ArrayList<Feat416Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat416(u)
    }
    return list
}
