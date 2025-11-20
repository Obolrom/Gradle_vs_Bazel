package com.romix.feature.feat98

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat98Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat98UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat98FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat98UserSummary
)

data class Feat98UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat98NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat98Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat98Config = Feat98Config()
) {

    fun loadSnapshot(userId: Long): Feat98NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat98NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat98UserSummary {
        return Feat98UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat98FeedItem> {
        val result = java.util.ArrayList<Feat98FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat98FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat98UiMapper {

    fun mapToUi(model: List<Feat98FeedItem>): Feat98UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat98UiModel(
            header = UiText("Feat98 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat98UiModel =
        Feat98UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat98UiModel =
        Feat98UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat98UiModel =
        Feat98UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat98Service(
    private val repository: Feat98Repository,
    private val uiMapper: Feat98UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat98UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat98UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat98UserItem1(val user: CoreUser, val label: String)
data class Feat98UserItem2(val user: CoreUser, val label: String)
data class Feat98UserItem3(val user: CoreUser, val label: String)
data class Feat98UserItem4(val user: CoreUser, val label: String)
data class Feat98UserItem5(val user: CoreUser, val label: String)
data class Feat98UserItem6(val user: CoreUser, val label: String)
data class Feat98UserItem7(val user: CoreUser, val label: String)
data class Feat98UserItem8(val user: CoreUser, val label: String)
data class Feat98UserItem9(val user: CoreUser, val label: String)
data class Feat98UserItem10(val user: CoreUser, val label: String)

data class Feat98StateBlock1(val state: Feat98UiModel, val checksum: Int)
data class Feat98StateBlock2(val state: Feat98UiModel, val checksum: Int)
data class Feat98StateBlock3(val state: Feat98UiModel, val checksum: Int)
data class Feat98StateBlock4(val state: Feat98UiModel, val checksum: Int)
data class Feat98StateBlock5(val state: Feat98UiModel, val checksum: Int)
data class Feat98StateBlock6(val state: Feat98UiModel, val checksum: Int)
data class Feat98StateBlock7(val state: Feat98UiModel, val checksum: Int)
data class Feat98StateBlock8(val state: Feat98UiModel, val checksum: Int)
data class Feat98StateBlock9(val state: Feat98UiModel, val checksum: Int)
data class Feat98StateBlock10(val state: Feat98UiModel, val checksum: Int)

fun buildFeat98UserItem(user: CoreUser, index: Int): Feat98UserItem1 {
    return Feat98UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat98StateBlock(model: Feat98UiModel): Feat98StateBlock1 {
    return Feat98StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat98UserSummary> {
    val list = java.util.ArrayList<Feat98UserSummary>(users.size)
    for (user in users) {
        list += Feat98UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat98UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat98UiModel {
    val summaries = (0 until count).map {
        Feat98UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat98UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat98UiModel> {
    val models = java.util.ArrayList<Feat98UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat98AnalyticsEvent1(val name: String, val value: String)
data class Feat98AnalyticsEvent2(val name: String, val value: String)
data class Feat98AnalyticsEvent3(val name: String, val value: String)
data class Feat98AnalyticsEvent4(val name: String, val value: String)
data class Feat98AnalyticsEvent5(val name: String, val value: String)
data class Feat98AnalyticsEvent6(val name: String, val value: String)
data class Feat98AnalyticsEvent7(val name: String, val value: String)
data class Feat98AnalyticsEvent8(val name: String, val value: String)
data class Feat98AnalyticsEvent9(val name: String, val value: String)
data class Feat98AnalyticsEvent10(val name: String, val value: String)

fun logFeat98Event1(event: Feat98AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat98Event2(event: Feat98AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat98Event3(event: Feat98AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat98Event4(event: Feat98AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat98Event5(event: Feat98AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat98Event6(event: Feat98AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat98Event7(event: Feat98AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat98Event8(event: Feat98AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat98Event9(event: Feat98AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat98Event10(event: Feat98AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat98Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat98Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat98Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat98Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat98Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat98Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat98Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat98Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat98Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat98Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat98(u: CoreUser): Feat98Projection1 =
    Feat98Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat98Projection1> {
    val list = java.util.ArrayList<Feat98Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat98(u)
    }
    return list
}
