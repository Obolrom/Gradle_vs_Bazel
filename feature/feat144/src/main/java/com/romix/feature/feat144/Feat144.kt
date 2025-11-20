package com.romix.feature.feat144

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat144Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat144UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat144FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat144UserSummary
)

data class Feat144UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat144NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat144Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat144Config = Feat144Config()
) {

    fun loadSnapshot(userId: Long): Feat144NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat144NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat144UserSummary {
        return Feat144UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat144FeedItem> {
        val result = java.util.ArrayList<Feat144FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat144FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat144UiMapper {

    fun mapToUi(model: List<Feat144FeedItem>): Feat144UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat144UiModel(
            header = UiText("Feat144 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat144UiModel =
        Feat144UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat144UiModel =
        Feat144UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat144UiModel =
        Feat144UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat144Service(
    private val repository: Feat144Repository,
    private val uiMapper: Feat144UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat144UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat144UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat144UserItem1(val user: CoreUser, val label: String)
data class Feat144UserItem2(val user: CoreUser, val label: String)
data class Feat144UserItem3(val user: CoreUser, val label: String)
data class Feat144UserItem4(val user: CoreUser, val label: String)
data class Feat144UserItem5(val user: CoreUser, val label: String)
data class Feat144UserItem6(val user: CoreUser, val label: String)
data class Feat144UserItem7(val user: CoreUser, val label: String)
data class Feat144UserItem8(val user: CoreUser, val label: String)
data class Feat144UserItem9(val user: CoreUser, val label: String)
data class Feat144UserItem10(val user: CoreUser, val label: String)

data class Feat144StateBlock1(val state: Feat144UiModel, val checksum: Int)
data class Feat144StateBlock2(val state: Feat144UiModel, val checksum: Int)
data class Feat144StateBlock3(val state: Feat144UiModel, val checksum: Int)
data class Feat144StateBlock4(val state: Feat144UiModel, val checksum: Int)
data class Feat144StateBlock5(val state: Feat144UiModel, val checksum: Int)
data class Feat144StateBlock6(val state: Feat144UiModel, val checksum: Int)
data class Feat144StateBlock7(val state: Feat144UiModel, val checksum: Int)
data class Feat144StateBlock8(val state: Feat144UiModel, val checksum: Int)
data class Feat144StateBlock9(val state: Feat144UiModel, val checksum: Int)
data class Feat144StateBlock10(val state: Feat144UiModel, val checksum: Int)

fun buildFeat144UserItem(user: CoreUser, index: Int): Feat144UserItem1 {
    return Feat144UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat144StateBlock(model: Feat144UiModel): Feat144StateBlock1 {
    return Feat144StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat144UserSummary> {
    val list = java.util.ArrayList<Feat144UserSummary>(users.size)
    for (user in users) {
        list += Feat144UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat144UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat144UiModel {
    val summaries = (0 until count).map {
        Feat144UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat144UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat144UiModel> {
    val models = java.util.ArrayList<Feat144UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat144AnalyticsEvent1(val name: String, val value: String)
data class Feat144AnalyticsEvent2(val name: String, val value: String)
data class Feat144AnalyticsEvent3(val name: String, val value: String)
data class Feat144AnalyticsEvent4(val name: String, val value: String)
data class Feat144AnalyticsEvent5(val name: String, val value: String)
data class Feat144AnalyticsEvent6(val name: String, val value: String)
data class Feat144AnalyticsEvent7(val name: String, val value: String)
data class Feat144AnalyticsEvent8(val name: String, val value: String)
data class Feat144AnalyticsEvent9(val name: String, val value: String)
data class Feat144AnalyticsEvent10(val name: String, val value: String)

fun logFeat144Event1(event: Feat144AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat144Event2(event: Feat144AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat144Event3(event: Feat144AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat144Event4(event: Feat144AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat144Event5(event: Feat144AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat144Event6(event: Feat144AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat144Event7(event: Feat144AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat144Event8(event: Feat144AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat144Event9(event: Feat144AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat144Event10(event: Feat144AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat144Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat144Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat144Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat144Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat144Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat144Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat144Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat144Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat144Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat144Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat144(u: CoreUser): Feat144Projection1 =
    Feat144Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat144Projection1> {
    val list = java.util.ArrayList<Feat144Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat144(u)
    }
    return list
}
