package com.romix.feature.feat151

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat151Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat151UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat151FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat151UserSummary
)

data class Feat151UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat151NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat151Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat151Config = Feat151Config()
) {

    fun loadSnapshot(userId: Long): Feat151NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat151NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat151UserSummary {
        return Feat151UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat151FeedItem> {
        val result = java.util.ArrayList<Feat151FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat151FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat151UiMapper {

    fun mapToUi(model: List<Feat151FeedItem>): Feat151UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat151UiModel(
            header = UiText("Feat151 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat151UiModel =
        Feat151UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat151UiModel =
        Feat151UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat151UiModel =
        Feat151UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat151Service(
    private val repository: Feat151Repository,
    private val uiMapper: Feat151UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat151UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat151UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat151UserItem1(val user: CoreUser, val label: String)
data class Feat151UserItem2(val user: CoreUser, val label: String)
data class Feat151UserItem3(val user: CoreUser, val label: String)
data class Feat151UserItem4(val user: CoreUser, val label: String)
data class Feat151UserItem5(val user: CoreUser, val label: String)
data class Feat151UserItem6(val user: CoreUser, val label: String)
data class Feat151UserItem7(val user: CoreUser, val label: String)
data class Feat151UserItem8(val user: CoreUser, val label: String)
data class Feat151UserItem9(val user: CoreUser, val label: String)
data class Feat151UserItem10(val user: CoreUser, val label: String)

data class Feat151StateBlock1(val state: Feat151UiModel, val checksum: Int)
data class Feat151StateBlock2(val state: Feat151UiModel, val checksum: Int)
data class Feat151StateBlock3(val state: Feat151UiModel, val checksum: Int)
data class Feat151StateBlock4(val state: Feat151UiModel, val checksum: Int)
data class Feat151StateBlock5(val state: Feat151UiModel, val checksum: Int)
data class Feat151StateBlock6(val state: Feat151UiModel, val checksum: Int)
data class Feat151StateBlock7(val state: Feat151UiModel, val checksum: Int)
data class Feat151StateBlock8(val state: Feat151UiModel, val checksum: Int)
data class Feat151StateBlock9(val state: Feat151UiModel, val checksum: Int)
data class Feat151StateBlock10(val state: Feat151UiModel, val checksum: Int)

fun buildFeat151UserItem(user: CoreUser, index: Int): Feat151UserItem1 {
    return Feat151UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat151StateBlock(model: Feat151UiModel): Feat151StateBlock1 {
    return Feat151StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat151UserSummary> {
    val list = java.util.ArrayList<Feat151UserSummary>(users.size)
    for (user in users) {
        list += Feat151UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat151UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat151UiModel {
    val summaries = (0 until count).map {
        Feat151UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat151UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat151UiModel> {
    val models = java.util.ArrayList<Feat151UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat151AnalyticsEvent1(val name: String, val value: String)
data class Feat151AnalyticsEvent2(val name: String, val value: String)
data class Feat151AnalyticsEvent3(val name: String, val value: String)
data class Feat151AnalyticsEvent4(val name: String, val value: String)
data class Feat151AnalyticsEvent5(val name: String, val value: String)
data class Feat151AnalyticsEvent6(val name: String, val value: String)
data class Feat151AnalyticsEvent7(val name: String, val value: String)
data class Feat151AnalyticsEvent8(val name: String, val value: String)
data class Feat151AnalyticsEvent9(val name: String, val value: String)
data class Feat151AnalyticsEvent10(val name: String, val value: String)

fun logFeat151Event1(event: Feat151AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat151Event2(event: Feat151AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat151Event3(event: Feat151AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat151Event4(event: Feat151AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat151Event5(event: Feat151AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat151Event6(event: Feat151AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat151Event7(event: Feat151AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat151Event8(event: Feat151AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat151Event9(event: Feat151AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat151Event10(event: Feat151AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat151Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat151Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat151Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat151Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat151Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat151Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat151Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat151Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat151Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat151Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat151(u: CoreUser): Feat151Projection1 =
    Feat151Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat151Projection1> {
    val list = java.util.ArrayList<Feat151Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat151(u)
    }
    return list
}
