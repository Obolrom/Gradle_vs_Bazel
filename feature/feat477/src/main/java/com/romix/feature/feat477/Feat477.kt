package com.romix.feature.feat477

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat477Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat477UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat477FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat477UserSummary
)

data class Feat477UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat477NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat477Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat477Config = Feat477Config()
) {

    fun loadSnapshot(userId: Long): Feat477NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat477NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat477UserSummary {
        return Feat477UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat477FeedItem> {
        val result = java.util.ArrayList<Feat477FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat477FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat477UiMapper {

    fun mapToUi(model: List<Feat477FeedItem>): Feat477UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat477UiModel(
            header = UiText("Feat477 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat477UiModel =
        Feat477UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat477UiModel =
        Feat477UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat477UiModel =
        Feat477UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat477Service(
    private val repository: Feat477Repository,
    private val uiMapper: Feat477UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat477UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat477UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat477UserItem1(val user: CoreUser, val label: String)
data class Feat477UserItem2(val user: CoreUser, val label: String)
data class Feat477UserItem3(val user: CoreUser, val label: String)
data class Feat477UserItem4(val user: CoreUser, val label: String)
data class Feat477UserItem5(val user: CoreUser, val label: String)
data class Feat477UserItem6(val user: CoreUser, val label: String)
data class Feat477UserItem7(val user: CoreUser, val label: String)
data class Feat477UserItem8(val user: CoreUser, val label: String)
data class Feat477UserItem9(val user: CoreUser, val label: String)
data class Feat477UserItem10(val user: CoreUser, val label: String)

data class Feat477StateBlock1(val state: Feat477UiModel, val checksum: Int)
data class Feat477StateBlock2(val state: Feat477UiModel, val checksum: Int)
data class Feat477StateBlock3(val state: Feat477UiModel, val checksum: Int)
data class Feat477StateBlock4(val state: Feat477UiModel, val checksum: Int)
data class Feat477StateBlock5(val state: Feat477UiModel, val checksum: Int)
data class Feat477StateBlock6(val state: Feat477UiModel, val checksum: Int)
data class Feat477StateBlock7(val state: Feat477UiModel, val checksum: Int)
data class Feat477StateBlock8(val state: Feat477UiModel, val checksum: Int)
data class Feat477StateBlock9(val state: Feat477UiModel, val checksum: Int)
data class Feat477StateBlock10(val state: Feat477UiModel, val checksum: Int)

fun buildFeat477UserItem(user: CoreUser, index: Int): Feat477UserItem1 {
    return Feat477UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat477StateBlock(model: Feat477UiModel): Feat477StateBlock1 {
    return Feat477StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat477UserSummary> {
    val list = java.util.ArrayList<Feat477UserSummary>(users.size)
    for (user in users) {
        list += Feat477UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat477UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat477UiModel {
    val summaries = (0 until count).map {
        Feat477UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat477UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat477UiModel> {
    val models = java.util.ArrayList<Feat477UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat477AnalyticsEvent1(val name: String, val value: String)
data class Feat477AnalyticsEvent2(val name: String, val value: String)
data class Feat477AnalyticsEvent3(val name: String, val value: String)
data class Feat477AnalyticsEvent4(val name: String, val value: String)
data class Feat477AnalyticsEvent5(val name: String, val value: String)
data class Feat477AnalyticsEvent6(val name: String, val value: String)
data class Feat477AnalyticsEvent7(val name: String, val value: String)
data class Feat477AnalyticsEvent8(val name: String, val value: String)
data class Feat477AnalyticsEvent9(val name: String, val value: String)
data class Feat477AnalyticsEvent10(val name: String, val value: String)

fun logFeat477Event1(event: Feat477AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat477Event2(event: Feat477AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat477Event3(event: Feat477AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat477Event4(event: Feat477AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat477Event5(event: Feat477AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat477Event6(event: Feat477AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat477Event7(event: Feat477AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat477Event8(event: Feat477AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat477Event9(event: Feat477AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat477Event10(event: Feat477AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat477Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat477Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat477Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat477Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat477Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat477Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat477Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat477Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat477Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat477Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat477(u: CoreUser): Feat477Projection1 =
    Feat477Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat477Projection1> {
    val list = java.util.ArrayList<Feat477Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat477(u)
    }
    return list
}
