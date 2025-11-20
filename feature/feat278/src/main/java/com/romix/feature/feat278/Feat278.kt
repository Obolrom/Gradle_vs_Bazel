package com.romix.feature.feat278

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat278Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat278UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat278FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat278UserSummary
)

data class Feat278UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat278NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat278Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat278Config = Feat278Config()
) {

    fun loadSnapshot(userId: Long): Feat278NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat278NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat278UserSummary {
        return Feat278UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat278FeedItem> {
        val result = java.util.ArrayList<Feat278FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat278FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat278UiMapper {

    fun mapToUi(model: List<Feat278FeedItem>): Feat278UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat278UiModel(
            header = UiText("Feat278 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat278UiModel =
        Feat278UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat278UiModel =
        Feat278UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat278UiModel =
        Feat278UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat278Service(
    private val repository: Feat278Repository,
    private val uiMapper: Feat278UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat278UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat278UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat278UserItem1(val user: CoreUser, val label: String)
data class Feat278UserItem2(val user: CoreUser, val label: String)
data class Feat278UserItem3(val user: CoreUser, val label: String)
data class Feat278UserItem4(val user: CoreUser, val label: String)
data class Feat278UserItem5(val user: CoreUser, val label: String)
data class Feat278UserItem6(val user: CoreUser, val label: String)
data class Feat278UserItem7(val user: CoreUser, val label: String)
data class Feat278UserItem8(val user: CoreUser, val label: String)
data class Feat278UserItem9(val user: CoreUser, val label: String)
data class Feat278UserItem10(val user: CoreUser, val label: String)

data class Feat278StateBlock1(val state: Feat278UiModel, val checksum: Int)
data class Feat278StateBlock2(val state: Feat278UiModel, val checksum: Int)
data class Feat278StateBlock3(val state: Feat278UiModel, val checksum: Int)
data class Feat278StateBlock4(val state: Feat278UiModel, val checksum: Int)
data class Feat278StateBlock5(val state: Feat278UiModel, val checksum: Int)
data class Feat278StateBlock6(val state: Feat278UiModel, val checksum: Int)
data class Feat278StateBlock7(val state: Feat278UiModel, val checksum: Int)
data class Feat278StateBlock8(val state: Feat278UiModel, val checksum: Int)
data class Feat278StateBlock9(val state: Feat278UiModel, val checksum: Int)
data class Feat278StateBlock10(val state: Feat278UiModel, val checksum: Int)

fun buildFeat278UserItem(user: CoreUser, index: Int): Feat278UserItem1 {
    return Feat278UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat278StateBlock(model: Feat278UiModel): Feat278StateBlock1 {
    return Feat278StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat278UserSummary> {
    val list = java.util.ArrayList<Feat278UserSummary>(users.size)
    for (user in users) {
        list += Feat278UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat278UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat278UiModel {
    val summaries = (0 until count).map {
        Feat278UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat278UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat278UiModel> {
    val models = java.util.ArrayList<Feat278UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat278AnalyticsEvent1(val name: String, val value: String)
data class Feat278AnalyticsEvent2(val name: String, val value: String)
data class Feat278AnalyticsEvent3(val name: String, val value: String)
data class Feat278AnalyticsEvent4(val name: String, val value: String)
data class Feat278AnalyticsEvent5(val name: String, val value: String)
data class Feat278AnalyticsEvent6(val name: String, val value: String)
data class Feat278AnalyticsEvent7(val name: String, val value: String)
data class Feat278AnalyticsEvent8(val name: String, val value: String)
data class Feat278AnalyticsEvent9(val name: String, val value: String)
data class Feat278AnalyticsEvent10(val name: String, val value: String)

fun logFeat278Event1(event: Feat278AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat278Event2(event: Feat278AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat278Event3(event: Feat278AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat278Event4(event: Feat278AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat278Event5(event: Feat278AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat278Event6(event: Feat278AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat278Event7(event: Feat278AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat278Event8(event: Feat278AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat278Event9(event: Feat278AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat278Event10(event: Feat278AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat278Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat278Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat278Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat278Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat278Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat278Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat278Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat278Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat278Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat278Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat278(u: CoreUser): Feat278Projection1 =
    Feat278Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat278Projection1> {
    val list = java.util.ArrayList<Feat278Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat278(u)
    }
    return list
}
