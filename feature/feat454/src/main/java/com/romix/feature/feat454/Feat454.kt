package com.romix.feature.feat454

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat454Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat454UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat454FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat454UserSummary
)

data class Feat454UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat454NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat454Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat454Config = Feat454Config()
) {

    fun loadSnapshot(userId: Long): Feat454NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat454NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat454UserSummary {
        return Feat454UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat454FeedItem> {
        val result = java.util.ArrayList<Feat454FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat454FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat454UiMapper {

    fun mapToUi(model: List<Feat454FeedItem>): Feat454UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat454UiModel(
            header = UiText("Feat454 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat454UiModel =
        Feat454UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat454UiModel =
        Feat454UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat454UiModel =
        Feat454UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat454Service(
    private val repository: Feat454Repository,
    private val uiMapper: Feat454UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat454UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat454UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat454UserItem1(val user: CoreUser, val label: String)
data class Feat454UserItem2(val user: CoreUser, val label: String)
data class Feat454UserItem3(val user: CoreUser, val label: String)
data class Feat454UserItem4(val user: CoreUser, val label: String)
data class Feat454UserItem5(val user: CoreUser, val label: String)
data class Feat454UserItem6(val user: CoreUser, val label: String)
data class Feat454UserItem7(val user: CoreUser, val label: String)
data class Feat454UserItem8(val user: CoreUser, val label: String)
data class Feat454UserItem9(val user: CoreUser, val label: String)
data class Feat454UserItem10(val user: CoreUser, val label: String)

data class Feat454StateBlock1(val state: Feat454UiModel, val checksum: Int)
data class Feat454StateBlock2(val state: Feat454UiModel, val checksum: Int)
data class Feat454StateBlock3(val state: Feat454UiModel, val checksum: Int)
data class Feat454StateBlock4(val state: Feat454UiModel, val checksum: Int)
data class Feat454StateBlock5(val state: Feat454UiModel, val checksum: Int)
data class Feat454StateBlock6(val state: Feat454UiModel, val checksum: Int)
data class Feat454StateBlock7(val state: Feat454UiModel, val checksum: Int)
data class Feat454StateBlock8(val state: Feat454UiModel, val checksum: Int)
data class Feat454StateBlock9(val state: Feat454UiModel, val checksum: Int)
data class Feat454StateBlock10(val state: Feat454UiModel, val checksum: Int)

fun buildFeat454UserItem(user: CoreUser, index: Int): Feat454UserItem1 {
    return Feat454UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat454StateBlock(model: Feat454UiModel): Feat454StateBlock1 {
    return Feat454StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat454UserSummary> {
    val list = java.util.ArrayList<Feat454UserSummary>(users.size)
    for (user in users) {
        list += Feat454UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat454UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat454UiModel {
    val summaries = (0 until count).map {
        Feat454UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat454UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat454UiModel> {
    val models = java.util.ArrayList<Feat454UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat454AnalyticsEvent1(val name: String, val value: String)
data class Feat454AnalyticsEvent2(val name: String, val value: String)
data class Feat454AnalyticsEvent3(val name: String, val value: String)
data class Feat454AnalyticsEvent4(val name: String, val value: String)
data class Feat454AnalyticsEvent5(val name: String, val value: String)
data class Feat454AnalyticsEvent6(val name: String, val value: String)
data class Feat454AnalyticsEvent7(val name: String, val value: String)
data class Feat454AnalyticsEvent8(val name: String, val value: String)
data class Feat454AnalyticsEvent9(val name: String, val value: String)
data class Feat454AnalyticsEvent10(val name: String, val value: String)

fun logFeat454Event1(event: Feat454AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat454Event2(event: Feat454AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat454Event3(event: Feat454AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat454Event4(event: Feat454AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat454Event5(event: Feat454AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat454Event6(event: Feat454AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat454Event7(event: Feat454AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat454Event8(event: Feat454AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat454Event9(event: Feat454AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat454Event10(event: Feat454AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat454Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat454Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat454Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat454Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat454Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat454Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat454Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat454Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat454Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat454Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat454(u: CoreUser): Feat454Projection1 =
    Feat454Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat454Projection1> {
    val list = java.util.ArrayList<Feat454Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat454(u)
    }
    return list
}
