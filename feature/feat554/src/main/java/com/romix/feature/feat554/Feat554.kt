package com.romix.feature.feat554

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat554Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat554UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat554FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat554UserSummary
)

data class Feat554UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat554NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat554Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat554Config = Feat554Config()
) {

    fun loadSnapshot(userId: Long): Feat554NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat554NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat554UserSummary {
        return Feat554UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat554FeedItem> {
        val result = java.util.ArrayList<Feat554FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat554FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat554UiMapper {

    fun mapToUi(model: List<Feat554FeedItem>): Feat554UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat554UiModel(
            header = UiText("Feat554 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat554UiModel =
        Feat554UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat554UiModel =
        Feat554UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat554UiModel =
        Feat554UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat554Service(
    private val repository: Feat554Repository,
    private val uiMapper: Feat554UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat554UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat554UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat554UserItem1(val user: CoreUser, val label: String)
data class Feat554UserItem2(val user: CoreUser, val label: String)
data class Feat554UserItem3(val user: CoreUser, val label: String)
data class Feat554UserItem4(val user: CoreUser, val label: String)
data class Feat554UserItem5(val user: CoreUser, val label: String)
data class Feat554UserItem6(val user: CoreUser, val label: String)
data class Feat554UserItem7(val user: CoreUser, val label: String)
data class Feat554UserItem8(val user: CoreUser, val label: String)
data class Feat554UserItem9(val user: CoreUser, val label: String)
data class Feat554UserItem10(val user: CoreUser, val label: String)

data class Feat554StateBlock1(val state: Feat554UiModel, val checksum: Int)
data class Feat554StateBlock2(val state: Feat554UiModel, val checksum: Int)
data class Feat554StateBlock3(val state: Feat554UiModel, val checksum: Int)
data class Feat554StateBlock4(val state: Feat554UiModel, val checksum: Int)
data class Feat554StateBlock5(val state: Feat554UiModel, val checksum: Int)
data class Feat554StateBlock6(val state: Feat554UiModel, val checksum: Int)
data class Feat554StateBlock7(val state: Feat554UiModel, val checksum: Int)
data class Feat554StateBlock8(val state: Feat554UiModel, val checksum: Int)
data class Feat554StateBlock9(val state: Feat554UiModel, val checksum: Int)
data class Feat554StateBlock10(val state: Feat554UiModel, val checksum: Int)

fun buildFeat554UserItem(user: CoreUser, index: Int): Feat554UserItem1 {
    return Feat554UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat554StateBlock(model: Feat554UiModel): Feat554StateBlock1 {
    return Feat554StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat554UserSummary> {
    val list = java.util.ArrayList<Feat554UserSummary>(users.size)
    for (user in users) {
        list += Feat554UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat554UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat554UiModel {
    val summaries = (0 until count).map {
        Feat554UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat554UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat554UiModel> {
    val models = java.util.ArrayList<Feat554UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat554AnalyticsEvent1(val name: String, val value: String)
data class Feat554AnalyticsEvent2(val name: String, val value: String)
data class Feat554AnalyticsEvent3(val name: String, val value: String)
data class Feat554AnalyticsEvent4(val name: String, val value: String)
data class Feat554AnalyticsEvent5(val name: String, val value: String)
data class Feat554AnalyticsEvent6(val name: String, val value: String)
data class Feat554AnalyticsEvent7(val name: String, val value: String)
data class Feat554AnalyticsEvent8(val name: String, val value: String)
data class Feat554AnalyticsEvent9(val name: String, val value: String)
data class Feat554AnalyticsEvent10(val name: String, val value: String)

fun logFeat554Event1(event: Feat554AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat554Event2(event: Feat554AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat554Event3(event: Feat554AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat554Event4(event: Feat554AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat554Event5(event: Feat554AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat554Event6(event: Feat554AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat554Event7(event: Feat554AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat554Event8(event: Feat554AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat554Event9(event: Feat554AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat554Event10(event: Feat554AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat554Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat554Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat554Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat554Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat554Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat554Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat554Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat554Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat554Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat554Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat554(u: CoreUser): Feat554Projection1 =
    Feat554Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat554Projection1> {
    val list = java.util.ArrayList<Feat554Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat554(u)
    }
    return list
}
