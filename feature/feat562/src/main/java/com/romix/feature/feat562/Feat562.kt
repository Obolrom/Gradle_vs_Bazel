package com.romix.feature.feat562

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat562Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat562UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat562FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat562UserSummary
)

data class Feat562UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat562NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat562Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat562Config = Feat562Config()
) {

    fun loadSnapshot(userId: Long): Feat562NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat562NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat562UserSummary {
        return Feat562UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat562FeedItem> {
        val result = java.util.ArrayList<Feat562FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat562FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat562UiMapper {

    fun mapToUi(model: List<Feat562FeedItem>): Feat562UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat562UiModel(
            header = UiText("Feat562 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat562UiModel =
        Feat562UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat562UiModel =
        Feat562UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat562UiModel =
        Feat562UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat562Service(
    private val repository: Feat562Repository,
    private val uiMapper: Feat562UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat562UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat562UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat562UserItem1(val user: CoreUser, val label: String)
data class Feat562UserItem2(val user: CoreUser, val label: String)
data class Feat562UserItem3(val user: CoreUser, val label: String)
data class Feat562UserItem4(val user: CoreUser, val label: String)
data class Feat562UserItem5(val user: CoreUser, val label: String)
data class Feat562UserItem6(val user: CoreUser, val label: String)
data class Feat562UserItem7(val user: CoreUser, val label: String)
data class Feat562UserItem8(val user: CoreUser, val label: String)
data class Feat562UserItem9(val user: CoreUser, val label: String)
data class Feat562UserItem10(val user: CoreUser, val label: String)

data class Feat562StateBlock1(val state: Feat562UiModel, val checksum: Int)
data class Feat562StateBlock2(val state: Feat562UiModel, val checksum: Int)
data class Feat562StateBlock3(val state: Feat562UiModel, val checksum: Int)
data class Feat562StateBlock4(val state: Feat562UiModel, val checksum: Int)
data class Feat562StateBlock5(val state: Feat562UiModel, val checksum: Int)
data class Feat562StateBlock6(val state: Feat562UiModel, val checksum: Int)
data class Feat562StateBlock7(val state: Feat562UiModel, val checksum: Int)
data class Feat562StateBlock8(val state: Feat562UiModel, val checksum: Int)
data class Feat562StateBlock9(val state: Feat562UiModel, val checksum: Int)
data class Feat562StateBlock10(val state: Feat562UiModel, val checksum: Int)

fun buildFeat562UserItem(user: CoreUser, index: Int): Feat562UserItem1 {
    return Feat562UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat562StateBlock(model: Feat562UiModel): Feat562StateBlock1 {
    return Feat562StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat562UserSummary> {
    val list = java.util.ArrayList<Feat562UserSummary>(users.size)
    for (user in users) {
        list += Feat562UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat562UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat562UiModel {
    val summaries = (0 until count).map {
        Feat562UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat562UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat562UiModel> {
    val models = java.util.ArrayList<Feat562UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat562AnalyticsEvent1(val name: String, val value: String)
data class Feat562AnalyticsEvent2(val name: String, val value: String)
data class Feat562AnalyticsEvent3(val name: String, val value: String)
data class Feat562AnalyticsEvent4(val name: String, val value: String)
data class Feat562AnalyticsEvent5(val name: String, val value: String)
data class Feat562AnalyticsEvent6(val name: String, val value: String)
data class Feat562AnalyticsEvent7(val name: String, val value: String)
data class Feat562AnalyticsEvent8(val name: String, val value: String)
data class Feat562AnalyticsEvent9(val name: String, val value: String)
data class Feat562AnalyticsEvent10(val name: String, val value: String)

fun logFeat562Event1(event: Feat562AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat562Event2(event: Feat562AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat562Event3(event: Feat562AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat562Event4(event: Feat562AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat562Event5(event: Feat562AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat562Event6(event: Feat562AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat562Event7(event: Feat562AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat562Event8(event: Feat562AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat562Event9(event: Feat562AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat562Event10(event: Feat562AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat562Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat562Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat562Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat562Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat562Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat562Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat562Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat562Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat562Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat562Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat562(u: CoreUser): Feat562Projection1 =
    Feat562Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat562Projection1> {
    val list = java.util.ArrayList<Feat562Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat562(u)
    }
    return list
}
