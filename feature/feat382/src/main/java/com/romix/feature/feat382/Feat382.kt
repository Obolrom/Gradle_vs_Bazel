package com.romix.feature.feat382

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat382Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat382UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat382FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat382UserSummary
)

data class Feat382UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat382NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat382Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat382Config = Feat382Config()
) {

    fun loadSnapshot(userId: Long): Feat382NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat382NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat382UserSummary {
        return Feat382UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat382FeedItem> {
        val result = java.util.ArrayList<Feat382FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat382FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat382UiMapper {

    fun mapToUi(model: List<Feat382FeedItem>): Feat382UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat382UiModel(
            header = UiText("Feat382 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat382UiModel =
        Feat382UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat382UiModel =
        Feat382UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat382UiModel =
        Feat382UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat382Service(
    private val repository: Feat382Repository,
    private val uiMapper: Feat382UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat382UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat382UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat382UserItem1(val user: CoreUser, val label: String)
data class Feat382UserItem2(val user: CoreUser, val label: String)
data class Feat382UserItem3(val user: CoreUser, val label: String)
data class Feat382UserItem4(val user: CoreUser, val label: String)
data class Feat382UserItem5(val user: CoreUser, val label: String)
data class Feat382UserItem6(val user: CoreUser, val label: String)
data class Feat382UserItem7(val user: CoreUser, val label: String)
data class Feat382UserItem8(val user: CoreUser, val label: String)
data class Feat382UserItem9(val user: CoreUser, val label: String)
data class Feat382UserItem10(val user: CoreUser, val label: String)

data class Feat382StateBlock1(val state: Feat382UiModel, val checksum: Int)
data class Feat382StateBlock2(val state: Feat382UiModel, val checksum: Int)
data class Feat382StateBlock3(val state: Feat382UiModel, val checksum: Int)
data class Feat382StateBlock4(val state: Feat382UiModel, val checksum: Int)
data class Feat382StateBlock5(val state: Feat382UiModel, val checksum: Int)
data class Feat382StateBlock6(val state: Feat382UiModel, val checksum: Int)
data class Feat382StateBlock7(val state: Feat382UiModel, val checksum: Int)
data class Feat382StateBlock8(val state: Feat382UiModel, val checksum: Int)
data class Feat382StateBlock9(val state: Feat382UiModel, val checksum: Int)
data class Feat382StateBlock10(val state: Feat382UiModel, val checksum: Int)

fun buildFeat382UserItem(user: CoreUser, index: Int): Feat382UserItem1 {
    return Feat382UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat382StateBlock(model: Feat382UiModel): Feat382StateBlock1 {
    return Feat382StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat382UserSummary> {
    val list = java.util.ArrayList<Feat382UserSummary>(users.size)
    for (user in users) {
        list += Feat382UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat382UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat382UiModel {
    val summaries = (0 until count).map {
        Feat382UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat382UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat382UiModel> {
    val models = java.util.ArrayList<Feat382UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat382AnalyticsEvent1(val name: String, val value: String)
data class Feat382AnalyticsEvent2(val name: String, val value: String)
data class Feat382AnalyticsEvent3(val name: String, val value: String)
data class Feat382AnalyticsEvent4(val name: String, val value: String)
data class Feat382AnalyticsEvent5(val name: String, val value: String)
data class Feat382AnalyticsEvent6(val name: String, val value: String)
data class Feat382AnalyticsEvent7(val name: String, val value: String)
data class Feat382AnalyticsEvent8(val name: String, val value: String)
data class Feat382AnalyticsEvent9(val name: String, val value: String)
data class Feat382AnalyticsEvent10(val name: String, val value: String)

fun logFeat382Event1(event: Feat382AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat382Event2(event: Feat382AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat382Event3(event: Feat382AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat382Event4(event: Feat382AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat382Event5(event: Feat382AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat382Event6(event: Feat382AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat382Event7(event: Feat382AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat382Event8(event: Feat382AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat382Event9(event: Feat382AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat382Event10(event: Feat382AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat382Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat382Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat382Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat382Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat382Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat382Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat382Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat382Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat382Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat382Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat382(u: CoreUser): Feat382Projection1 =
    Feat382Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat382Projection1> {
    val list = java.util.ArrayList<Feat382Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat382(u)
    }
    return list
}
