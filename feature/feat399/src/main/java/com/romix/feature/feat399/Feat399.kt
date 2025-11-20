package com.romix.feature.feat399

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat399Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat399UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat399FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat399UserSummary
)

data class Feat399UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat399NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat399Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat399Config = Feat399Config()
) {

    fun loadSnapshot(userId: Long): Feat399NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat399NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat399UserSummary {
        return Feat399UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat399FeedItem> {
        val result = java.util.ArrayList<Feat399FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat399FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat399UiMapper {

    fun mapToUi(model: List<Feat399FeedItem>): Feat399UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat399UiModel(
            header = UiText("Feat399 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat399UiModel =
        Feat399UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat399UiModel =
        Feat399UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat399UiModel =
        Feat399UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat399Service(
    private val repository: Feat399Repository,
    private val uiMapper: Feat399UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat399UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat399UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat399UserItem1(val user: CoreUser, val label: String)
data class Feat399UserItem2(val user: CoreUser, val label: String)
data class Feat399UserItem3(val user: CoreUser, val label: String)
data class Feat399UserItem4(val user: CoreUser, val label: String)
data class Feat399UserItem5(val user: CoreUser, val label: String)
data class Feat399UserItem6(val user: CoreUser, val label: String)
data class Feat399UserItem7(val user: CoreUser, val label: String)
data class Feat399UserItem8(val user: CoreUser, val label: String)
data class Feat399UserItem9(val user: CoreUser, val label: String)
data class Feat399UserItem10(val user: CoreUser, val label: String)

data class Feat399StateBlock1(val state: Feat399UiModel, val checksum: Int)
data class Feat399StateBlock2(val state: Feat399UiModel, val checksum: Int)
data class Feat399StateBlock3(val state: Feat399UiModel, val checksum: Int)
data class Feat399StateBlock4(val state: Feat399UiModel, val checksum: Int)
data class Feat399StateBlock5(val state: Feat399UiModel, val checksum: Int)
data class Feat399StateBlock6(val state: Feat399UiModel, val checksum: Int)
data class Feat399StateBlock7(val state: Feat399UiModel, val checksum: Int)
data class Feat399StateBlock8(val state: Feat399UiModel, val checksum: Int)
data class Feat399StateBlock9(val state: Feat399UiModel, val checksum: Int)
data class Feat399StateBlock10(val state: Feat399UiModel, val checksum: Int)

fun buildFeat399UserItem(user: CoreUser, index: Int): Feat399UserItem1 {
    return Feat399UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat399StateBlock(model: Feat399UiModel): Feat399StateBlock1 {
    return Feat399StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat399UserSummary> {
    val list = java.util.ArrayList<Feat399UserSummary>(users.size)
    for (user in users) {
        list += Feat399UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat399UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat399UiModel {
    val summaries = (0 until count).map {
        Feat399UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat399UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat399UiModel> {
    val models = java.util.ArrayList<Feat399UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat399AnalyticsEvent1(val name: String, val value: String)
data class Feat399AnalyticsEvent2(val name: String, val value: String)
data class Feat399AnalyticsEvent3(val name: String, val value: String)
data class Feat399AnalyticsEvent4(val name: String, val value: String)
data class Feat399AnalyticsEvent5(val name: String, val value: String)
data class Feat399AnalyticsEvent6(val name: String, val value: String)
data class Feat399AnalyticsEvent7(val name: String, val value: String)
data class Feat399AnalyticsEvent8(val name: String, val value: String)
data class Feat399AnalyticsEvent9(val name: String, val value: String)
data class Feat399AnalyticsEvent10(val name: String, val value: String)

fun logFeat399Event1(event: Feat399AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat399Event2(event: Feat399AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat399Event3(event: Feat399AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat399Event4(event: Feat399AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat399Event5(event: Feat399AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat399Event6(event: Feat399AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat399Event7(event: Feat399AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat399Event8(event: Feat399AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat399Event9(event: Feat399AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat399Event10(event: Feat399AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat399Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat399Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat399Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat399Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat399Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat399Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat399Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat399Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat399Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat399Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat399(u: CoreUser): Feat399Projection1 =
    Feat399Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat399Projection1> {
    val list = java.util.ArrayList<Feat399Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat399(u)
    }
    return list
}
