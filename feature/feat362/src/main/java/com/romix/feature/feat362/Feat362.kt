package com.romix.feature.feat362

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat362Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat362UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat362FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat362UserSummary
)

data class Feat362UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat362NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat362Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat362Config = Feat362Config()
) {

    fun loadSnapshot(userId: Long): Feat362NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat362NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat362UserSummary {
        return Feat362UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat362FeedItem> {
        val result = java.util.ArrayList<Feat362FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat362FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat362UiMapper {

    fun mapToUi(model: List<Feat362FeedItem>): Feat362UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat362UiModel(
            header = UiText("Feat362 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat362UiModel =
        Feat362UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat362UiModel =
        Feat362UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat362UiModel =
        Feat362UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat362Service(
    private val repository: Feat362Repository,
    private val uiMapper: Feat362UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat362UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat362UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat362UserItem1(val user: CoreUser, val label: String)
data class Feat362UserItem2(val user: CoreUser, val label: String)
data class Feat362UserItem3(val user: CoreUser, val label: String)
data class Feat362UserItem4(val user: CoreUser, val label: String)
data class Feat362UserItem5(val user: CoreUser, val label: String)
data class Feat362UserItem6(val user: CoreUser, val label: String)
data class Feat362UserItem7(val user: CoreUser, val label: String)
data class Feat362UserItem8(val user: CoreUser, val label: String)
data class Feat362UserItem9(val user: CoreUser, val label: String)
data class Feat362UserItem10(val user: CoreUser, val label: String)

data class Feat362StateBlock1(val state: Feat362UiModel, val checksum: Int)
data class Feat362StateBlock2(val state: Feat362UiModel, val checksum: Int)
data class Feat362StateBlock3(val state: Feat362UiModel, val checksum: Int)
data class Feat362StateBlock4(val state: Feat362UiModel, val checksum: Int)
data class Feat362StateBlock5(val state: Feat362UiModel, val checksum: Int)
data class Feat362StateBlock6(val state: Feat362UiModel, val checksum: Int)
data class Feat362StateBlock7(val state: Feat362UiModel, val checksum: Int)
data class Feat362StateBlock8(val state: Feat362UiModel, val checksum: Int)
data class Feat362StateBlock9(val state: Feat362UiModel, val checksum: Int)
data class Feat362StateBlock10(val state: Feat362UiModel, val checksum: Int)

fun buildFeat362UserItem(user: CoreUser, index: Int): Feat362UserItem1 {
    return Feat362UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat362StateBlock(model: Feat362UiModel): Feat362StateBlock1 {
    return Feat362StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat362UserSummary> {
    val list = java.util.ArrayList<Feat362UserSummary>(users.size)
    for (user in users) {
        list += Feat362UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat362UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat362UiModel {
    val summaries = (0 until count).map {
        Feat362UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat362UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat362UiModel> {
    val models = java.util.ArrayList<Feat362UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat362AnalyticsEvent1(val name: String, val value: String)
data class Feat362AnalyticsEvent2(val name: String, val value: String)
data class Feat362AnalyticsEvent3(val name: String, val value: String)
data class Feat362AnalyticsEvent4(val name: String, val value: String)
data class Feat362AnalyticsEvent5(val name: String, val value: String)
data class Feat362AnalyticsEvent6(val name: String, val value: String)
data class Feat362AnalyticsEvent7(val name: String, val value: String)
data class Feat362AnalyticsEvent8(val name: String, val value: String)
data class Feat362AnalyticsEvent9(val name: String, val value: String)
data class Feat362AnalyticsEvent10(val name: String, val value: String)

fun logFeat362Event1(event: Feat362AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat362Event2(event: Feat362AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat362Event3(event: Feat362AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat362Event4(event: Feat362AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat362Event5(event: Feat362AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat362Event6(event: Feat362AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat362Event7(event: Feat362AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat362Event8(event: Feat362AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat362Event9(event: Feat362AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat362Event10(event: Feat362AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat362Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat362Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat362Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat362Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat362Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat362Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat362Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat362Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat362Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat362Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat362(u: CoreUser): Feat362Projection1 =
    Feat362Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat362Projection1> {
    val list = java.util.ArrayList<Feat362Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat362(u)
    }
    return list
}
